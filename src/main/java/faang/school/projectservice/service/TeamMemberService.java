package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CreateTeamMemberDto;
import faang.school.projectservice.dto.ResponseTeamMemberDto;
import faang.school.projectservice.dto.UpdateTeamMemberDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.ResourceNotFoundException;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamMemberActions;
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
    private final UserContext userContext;

    @Transactional
    public ResponseTeamMemberDto addTeamMember(CreateTeamMemberDto teamMemberDto,
                                               long teamId) {
        long creatorId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        Project project = team.getProject();
        ensureHasAccess(creatorId, List.of(TeamRole.OWNER, TeamRole.TEAMLEAD), TeamMemberActions.ADD, project.getId());

        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDto);
        teamMember = createTeamMember(teamMember, team, project);
        teamService.saveTeam(team);
        teamMemberRepository.save(teamMember);
        return teamMemberMapper.toResponseDto(teamMember);
    }

    @Transactional
    public ResponseTeamMemberDto updateTeamMember(UpdateTeamMemberDto teamMemberDto,
                                                  long teamId,
                                                  long memberId) {
        long updaterId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        TeamMember updater = teamMemberRepository.findById(updaterId);
        TeamMember memberToUpdate = teamMemberRepository.findById(memberId);

        if (updater.getRoles().contains(TeamRole.TEAMLEAD)) {
            if (teamMemberDto.roles() != null) {
                memberToUpdate.setRoles(teamMemberDto.roles());
            }
            if (teamMemberDto.stageIds() != null) {
                List<Stage> stages = stageService.getStagesByIds(teamMemberDto.stageIds());
                memberToUpdate.setStages(stages);
            }
        } else if (updaterId == memberId) {
            UserDto user = userServiceClient.getUser(updaterId);
            user.setUsername(teamMemberDto.username());
        }

        teamService.saveTeam(team);
        teamMemberRepository.save(memberToUpdate);
        return teamMemberMapper.toResponseDto(memberToUpdate);
    }

    @Transactional
    public void deleteTeamMember(long memberId, long teamId) {
        long deleterId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        Project project = team.getProject();
        ensureHasAccess(deleterId, List.of(TeamRole.OWNER), TeamMemberActions.REMOVE, project.getId());
        TeamMember teamMember = teamMemberRepository.findById(memberId);
        teamMemberRepository.removeTeamMemberFromTeam(team.getId(), teamMember);
        teamService.saveTeam(teamMember.getTeam());
        teamMemberRepository.delete(teamMember);
    }

    public List<ResponseTeamMemberDto> getFilteredTeamMembers(String name, TeamRole role, long projectId) {
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

        return teamMemberMapper.toResponseDto(filteredMembers);
    }

    public List<ResponseTeamMemberDto> getTeamMembersByTeamId(long teamId) {
        Team team = teamService.getTeamById(teamId);
        return teamMemberMapper.toResponseDto(team.getTeamMembers());
    }

    public boolean curatorHasNoAccess(Long curatorId) {
        TeamMember teamMember = teamMemberRepository.findById(curatorId);
        return !teamMember.isCurator();
    }

    private boolean TeamMemberExistsByUserAndProjectIds(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId) != null;
    }

    private boolean hasAccess(Long userId, List<TeamRole> requiredRoles, Long projectId) {
        if (TeamMemberExistsByUserAndProjectIds(userId, projectId)) {
            TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
            return requiredRoles.stream().anyMatch(role -> teamMember.getRoles().contains(role));
        } else {
            log.warn("Team member with user ID {} does not exist", userId);
            throw new ResourceNotFoundException(
                    String.format("Team member with user ID %d does not exist", userId)
            );
        }
    }

    private void ensureHasAccess(Long userId, List<TeamRole> requiredRoles,
                                 TeamMemberActions action, Long projectId) {
        if (!hasAccess(userId, requiredRoles, projectId)) {
            log.warn("Member with ID {} has no access to {}", userId, action.getDescription());
            throw new DataValidationException(String.format(
                    "Member with ID %d has no access to %s", userId, action.getDescription())
            );
        }
    }

    private boolean isUserAlreadyInProject(Long memberId, Project project) {
        return project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.getId().equals(memberId))
                );
    }

    private TeamMember createTeamMember(TeamMember teamMember, Team team, Project project) {
        if (isUserAlreadyInProject(teamMember.getId(), project)) {
            TeamMember existingTeamMember = teamMemberRepository.findById(teamMember.getId());
            existingTeamMember.setRoles(teamMember.getRoles());
            return existingTeamMember;
        }
        teamMember.setTeam(team);
        teamMemberRepository.addTeamMemberToTeam(team.getId(), teamMember);
        return teamMember;
    }
}
