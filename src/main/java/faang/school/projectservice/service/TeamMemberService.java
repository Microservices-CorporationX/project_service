package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final StageService stageService;
    private final TeamMemberMapper teamMemberMapper;
    private final UserServiceClient userServiceClient;

    @Transactional
    public CreateTeamMemberDto addTeamMember(CreateTeamMemberDto teamMemberDto, Long creatorId) {
        ensureHasAccess(creatorId, List.of(TeamRole.OWNER, TeamRole.TEAMLEAD), "add team member");

        TeamMember teamMember = teamMemberMapper.toCreateEntity(teamMemberDto);
        Team team = teamService.getTeamById(teamMemberDto.teamId());
        Project project = team.getProject();

        if (isUserAlreadyInProject(teamMember.getId(), project)) {
            teamMemberRepository.findById(teamMember.getId()).setRoles(teamMember.getRoles());
            return teamMemberMapper.toCreateDto(teamMember);
        }

        teamMember.setTeam(team);
        team.addTeamMember(teamMember);
        teamService.saveTeam(team);
        return teamMemberMapper.toCreateDto(teamMember);
    }

    @Transactional
    public UpdateTeamMemberDto updateTeamMember(UpdateTeamMemberDto teamMemberDto, Long updaterId) {
        TeamMember updater = teamMemberRepository.findById(updaterId);
        TeamMember memberToUpdate = teamMemberRepository.findById(teamMemberDto.id());

        if (updater.getRoles().contains(TeamRole.TEAMLEAD)) {
            if (teamMemberDto.roles() != null) {
                memberToUpdate.setRoles(teamMemberDto.roles());
            }
            if (teamMemberDto.stageIds()!=null) {
                List<Stage> stages = stageService.getStagesByIds(teamMemberDto.stageIds());
                memberToUpdate.setStages(stages);
            }
        } else if (updaterId.equals(teamMemberDto.id())) {
            UserDto user = userServiceClient.getUser(updaterId);
            user.setUsername(teamMemberDto.username());
        }

        return teamMemberMapper.toUpdateDto(memberToUpdate);
    }

    @Transactional
    public void deleteTeamMember(long memberId, long deleterId) {
        ensureHasAccess(deleterId, List.of(TeamRole.OWNER), "delete team member");
        TeamMember teamMember = teamMemberRepository.findById(memberId);
        teamMember.getTeam().removeTeamMember(teamMember);
    }

    public List<CreateTeamMemberDto> getFilteredTeamMembers(String name, TeamRole role, long projectId) {
        List<Team> teams = projectService.findProjectById(projectId).getTeams();
        List<Long> userIds = teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();

        List<UserDto> users = userServiceClient.getUsersByIds(userIds);

        List<TeamMember> filteredMembers = teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> {
                    UserDto user = users.stream()
                            .filter(u -> u.getId().equals(member.getId()))
                            .findFirst()
                            .orElseThrow(() -> new DataValidationException("User not found"));
                    return user.getUsername().contains(name) && member.getRoles().contains(role);
                })
                .toList();

        return teamMemberMapper.toCreateDtoList(filteredMembers);
    }

    public List<CreateTeamMemberDto> getAllTeamMembers() {
        List<Team> teams = teamService.getAllTeams();
        List<TeamMember> members = teams.stream()
                .map(Team::getTeamMembers)
                .flatMap(List::stream)
                .toList();
        return teamMemberMapper.toCreateDtoList(members);
    }

    public List<CreateTeamMemberDto> getTeamMembersByTeamId(long teamId) {
        Team team = teamService.getTeamById(teamId);
        return teamMemberMapper.toCreateDtoList(team.getTeamMembers());
    }

    public boolean curatorHasNoAccess(Long curatorId) {
        TeamMember teamMember = teamMemberRepository.findById(curatorId);
        return !teamMember.isCurator();
    }

    private boolean hasAccess(Long memberId, List<TeamRole> requiredRoles) {
        TeamMember teamMember = teamMemberRepository.findById(memberId);
        return requiredRoles.stream().anyMatch(role -> teamMember.getRoles().contains(role));
    }

    private void ensureHasAccess(Long memberId, List<TeamRole> requiredRoles, String action) {
        if (!hasAccess(memberId, requiredRoles)) {
            log.warn("Member with ID {} has no access to {}", memberId, action);
            throw new DataValidationException(String.format("Member with ID %d has no access to %s", memberId, action));
        }
    }

    private boolean isUserAlreadyInProject(Long memberId, Project project) {
        return project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getId().equals(memberId))
                );
    }
}
