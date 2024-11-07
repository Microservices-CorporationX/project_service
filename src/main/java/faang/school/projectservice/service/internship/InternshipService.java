package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipDtoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipDtoValidator internshipDtoValidator;
    private final InternshipMapper internshipMapper;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;

    @Transactional
    public InternshipDto createInternship(InternshipCreationDto internshipCreationDto) {
        TeamMember mentor = internshipDtoValidator.validateCreationDtoAndGetMentor(internshipCreationDto);

        Internship internship = internshipMapper.toEntity(internshipCreationDto);
        Project project = mentor.getTeam().getProject();

        Team team = teamService.save(
                Team.builder()
                        .project(project)
                        .build()
        );
        List<TeamMember> interns = createTeamMembers(internshipCreationDto.getInternUserIds(), team, TeamRole.INTERN);

        internship.setInterns(interns);
        internship.setMentorId(mentor);
        internship.setProject(project);
        internship.setStatus(InternshipStatus.NOT_STARTED);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipUpdateRequestDto updateInternship(InternshipUpdateDto updateDto) {
        Internship internship = internshipDtoValidator.validateUpdateDtoAndGetInternship(updateDto);

        List<TeamMember> interns = internship.getInterns();
        Team internsProjectTeam = interns.get(0).getTeam();
        List<TeamMember> teamMembers = internsProjectTeam.getTeamMembers();
        List<Task> tasks = internsProjectTeam.getProject().getTasks();

        Set<Long> internsUserIds = getTeamMembersIds(interns);
        Set<Long> idsOfUsersWithDoneTasks = getIdsOfUsersWithDoneTasks(internsUserIds, tasks);

        updateTeamMembersRoles(teamMembers, idsOfUsersWithDoneTasks, updateDto.getInternNewTeamRole());
        updateInternshipStatusAndProjectTeam(internship, teamMembers, idsOfUsersWithDoneTasks);

        teamService.save(internsProjectTeam);
        internshipRepository.save(internship);

        return InternshipUpdateRequestDto.builder()
                .id(internship.getId())
                .idsOfUsersWithCompletedTasks(idsOfUsersWithDoneTasks.stream().toList())
                .internNewTeamRole(updateDto.getInternNewTeamRole())
                .internshipStatus(internship.getStatus())
                .build();
    }

    private Set<Long> getTeamMembersIds(Collection<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());
    }

    private void updateTeamMembersRoles(Collection<TeamMember> teamMembers, Collection<Long> idsOfUsersWithDoneTasks,
                                        TeamRole newTeamRole) {
        teamMembers.stream()
                .filter(intern -> idsOfUsersWithDoneTasks.contains(intern.getUserId()))
                .forEach(intern -> intern.getRoles()
                        .replaceAll(role -> role == TeamRole.INTERN ? newTeamRole : role));
    }

    private void updateInternshipStatusAndProjectTeam(Internship internship, Collection<TeamMember> teamMembers,
                                                      Collection<Long> idsOfUsersWithDoneTasks) {
        if (LocalDateTime.now().isAfter(internship.getEndDate())) {
            internship.setStatus(InternshipStatus.COMPLETED);
            teamMembers.removeIf(teamMember -> !idsOfUsersWithDoneTasks.contains(teamMember.getUserId()));
        } else {
            internship.setStatus(InternshipStatus.IN_PROGRESS);
        }
    }

    private List<TeamMember> createTeamMembers(List<Long> userIds, Team team, TeamRole role) {
        return userIds.stream()
                .map(userId -> teamMemberService.save(
                        TeamMember.builder()
                                .userId(userId)
                                .roles(List.of(role))
                                .team(team)
                                .build()
                )).toList();
    }

    private Set<Long> getIdsOfUsersWithDoneTasks(Collection<Long> userIds, List<Task> tasks) {
        Set<Long> userIdsSet = new HashSet<>(userIds);

        return tasks.stream()
                .filter(task -> userIdsSet.contains(task.getPerformerUserId()))
                .collect(Collectors.groupingBy(Task::getPerformerUserId))
                .entrySet().stream()
                .filter(taskToPerformer ->
                        taskToPerformer.getValue().stream().allMatch(task -> task.getStatus() == TaskStatus.DONE)
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
