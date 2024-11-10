package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
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
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipDtoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipDtoValidator internshipDtoValidator;
    private final InternshipMapper internshipMapper;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipDto createInternship(InternshipCreationDto internshipCreationDto) {
        log.info("Received request to create internship for project ID {}", internshipCreationDto.getProjectId());

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

        Internship savedInternship = internshipRepository.save(internship);
        log.info("Created internship with ID {} for project ID {}",
                savedInternship.getId(), internshipCreationDto.getProjectId()
        );

        return internshipMapper.toDto(savedInternship);
    }

    @Transactional
    public InternshipUpdateRequestDto updateInternship(InternshipUpdateDto updateDto) {
        log.info("Received request to update internship status, internship ID {}", updateDto.getInternshipId());
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

        log.info("The status of an internship with ID {} was updated. The project team was updated too.",
                updateDto.getInternshipId()
        );
        return InternshipUpdateRequestDto.builder()
                .id(internship.getId())
                .idsOfUsersWithCompletedTasks(idsOfUsersWithDoneTasks.stream().toList())
                .internNewTeamRole(updateDto.getInternNewTeamRole())
                .internshipStatus(internship.getStatus())
                .build();
    }

    public List<InternshipDto> getFilteredInternships(InternshipFilterDto filterDto) {
        log.info("Received request to get internships based on provided filters.");

        List<Internship> allInternships = internshipRepository.findAll();
        List<Internship> filteredInternships = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                        allInternships.stream(),
                        (internships, filter) -> filter.apply(internships, filterDto),
                        (s1, s2) -> s2
                )
                .peek(internship -> log.debug("Filtered internship: ID={}, Status={}",
                        internship.getId(), internship.getStatus()))
                .toList();

        log.info("Filtered internships: total={}, matching criteria={}", allInternships.size(), filteredInternships.size());

        return internshipMapper.toDto(filteredInternships);
    }

    public List<InternshipDto> getAllInternships() {
        log.info("Received request to get all internships.");
        return internshipMapper.toDto(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        log.info("Received request to get internship by its ID: {}.", internshipId);
        return internshipMapper.toDto(
                internshipRepository.findById(internshipId)
                        .orElseThrow(() -> new DataValidationException(
                                "There is no internship with ID (%d) in the database!".formatted(internshipId))
                        )
        );
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