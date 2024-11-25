package faang.school.projectservice.service.teammember;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.teammember.TeamMemberDeleteDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.teammember.TeamMemberFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team_member.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {

    private static final String TEAM_MEMBER = "TeamMember";

    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberValidator teamMemberValidator;
    private final ProjectService projectService;
    private final TeamService teamService;
    private final List<TeamMemberFilter> teamMemberFilters;
    private final UserServiceClient userServiceClient;

    @Transactional
    public TeamMemberDto addMemberToTheTeam(TeamMemberDto teamMemberDto) {
        log.info("Adding team member to the project: {}", teamMemberDto.getTeam());

        Project project = projectService.getProjectById(teamMemberDto.getProjectId());
        TeamMember currentUser = findById(teamMemberDto.getCurrentUserId());

        if (!hasPermissionToAddMember(project, currentUser, teamMemberDto)) {
            throw new DataValidationException("You are not authorized to add team members");
        }

        Optional<TeamMember> existingMember = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(teamMemberDto.getUserId()))
                .findFirst();

        if (existingMember.isPresent()) {
            return updateExistingMember(existingMember.get(), teamMemberDto);
        } else {
            return addNewMember(teamMemberDto);
        }
    }

    @Transactional
    public TeamMemberDto updateMemberInTheTeam(TeamMemberUpdateDto teamMemberUpdateDto) {
        Team team = teamService.findById(teamMemberUpdateDto.getTeamId());
        TeamMember updateUser = findById(teamMemberUpdateDto.getUpdateUserId());
        TeamMember currentUser = findById(teamMemberUpdateDto.getCurrentUserId());

        if (currentUser.getRoles().contains(TeamRole.TEAMLEAD)) {
            return updateMemberAsTeamLead(teamMemberUpdateDto, team, updateUser);
        } else if (currentUser.getUserId().equals(teamMemberUpdateDto.getUpdateUserId())) {
            return updateMemberAsUser(teamMemberUpdateDto, updateUser);
        } else {
            throw new DataValidationException("You do not have permission to update this team member");
        }
    }

    @Transactional
    public void deleteMemberFromTheTeam(TeamMemberDeleteDto teamMemberDeleteDto) {
        log.info("Deleting team members from the project: {}", teamMemberDeleteDto);

        Project project = projectService.getProjectById(teamMemberDeleteDto.getProjectId());

        if (!project.getOwnerId().equals(teamMemberDeleteDto.getCurrentUserId())) {
            throw new DataValidationException("You are not authorized to delete team members");
        }

        deleteById(teamMemberDeleteDto.getDeleteUserId());
    }

    public List<TeamMemberDto> getAllMembersWithFilter(TeamMemberFilterDto teamMemberFilterDto) {
        Stream<TeamMember> teamMemberStream = findAll().stream();

        return teamMemberFilters.stream()
                .filter(filter -> filter.isApplicable(teamMemberFilterDto))
                .reduce(teamMemberStream, ((stream, filter) -> filter.apply(stream, teamMemberFilterDto)), (e1, e2) -> e1)
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public List<TeamMemberDto> getAllMembersFromTheProject(Long projectId) {
        log.info("Retrieving all team members for project: {}", projectId);
        Project project = projectService.getProjectById(projectId);
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(teamMemberMapper::toDto)
                .toList();
    }

    public TeamMemberDto getMemberById(Long id) {
        log.debug("Retrieving team member by ID: {}", id);
        teamMemberValidator.validationOnNullLessThanOrEqualToZero(id, "Id null or less than zero");
        TeamMember teamMember = findById(id);
        log.info("Team member successfully retrieved, ID: {}", id);
        return teamMemberMapper.toDto(teamMember);
    }


    private TeamMemberDto updateMemberAsTeamLead(TeamMemberUpdateDto dto, Team team, TeamMember updateUser) {
        UserDto user = userServiceClient.getUser(dto.getUpdateUserId());

        List<TeamRole> newRoles = dto.getRoles().stream()
                .map(TeamRole::valueOf)
                .toList();

        updateUser.setTeam(team);

        List<TeamRole> updatedRoles = new ArrayList<>(updateUser.getRoles());
        updatedRoles.addAll(newRoles);
        updateUser.setRoles(updatedRoles);

        TeamMember savedMember = save(updateUser);

        user.setUsername(dto.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        userServiceClient.saveUser(user);

        return teamMemberMapper.toDto(savedMember);
    }

    private TeamMemberDto updateMemberAsUser(TeamMemberUpdateDto dto, TeamMember updateUser) {
        UserDto user = userServiceClient.getUser(dto.getUpdateUserId());
        user.setUsername(dto.getUsername());
        user.setUpdatedAt(LocalDateTime.now());
        userServiceClient.saveUser(user);

        return teamMemberMapper.toDto(updateUser);
    }

    private boolean hasPermissionToAddMember(Project project, TeamMember currentUser, TeamMemberDto teamMemberDto) {
        return project.getOwnerId().equals(teamMemberDto.getCurrentUserId()) ||
                currentUser.getRoles().stream().anyMatch(role -> role == TeamRole.TEAMLEAD);
    }

    private TeamMemberDto updateExistingMember(TeamMember existingMember, TeamMemberDto teamMemberDto) {
        List<TeamRole> newRoles = teamMemberDto.getRole().stream()
                .map(TeamRole::valueOf)
                .collect(Collectors.toList());

        existingMember.setRoles(newRoles);
        TeamMember updatedMember = teamMemberRepository.save(existingMember);

        return teamMemberMapper.toDto(updatedMember);
    }

    private TeamMemberDto addNewMember(TeamMemberDto teamMemberDto) {
        Team team = teamService.findById(teamMemberDto.getTeam());
        List<TeamRole> teamRoles = teamMemberDto.getRole().stream()
                .map(TeamRole::valueOf)
                .toList();
        TeamMember teamMember = TeamMember.builder()
                .userId(teamMemberDto.getUserId())
                .roles(teamRoles)
                .team(team)
                .build();

        TeamMember savedMember = save(teamMember);
        log.info("New team member added successfully: {}", savedMember.getUserId());

        return teamMemberMapper.toDto(savedMember);
    }

    public TeamMember findById(long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER, id));
    }

    public List<TeamMember> findAllById(List<Long> id) {
        return teamMemberRepository.findAllById(id);
    }

    @Transactional
    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    @Transactional
    public List<TeamMember> saveAll(List<TeamMember> teamMembers) {
        return teamMemberRepository.saveAll(teamMembers);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER, userId));
    }

    public List<TeamMember> findAll() {
        return teamMemberRepository.findAll();
    }

    @Transactional
    public void deleteById(long id) {
        teamMemberRepository.deleteById(id);
    }

    @Transactional
    public void delete(TeamMember teamMember) {
        teamMemberRepository.delete(teamMember);
    }

    @Transactional
    public void deleteAll(List<TeamMember> teamMembers) {
        teamMemberRepository.deleteAll(teamMembers);
    }
}
