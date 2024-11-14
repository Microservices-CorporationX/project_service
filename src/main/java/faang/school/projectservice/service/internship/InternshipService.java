package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreationDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.dto.user.UserIdsDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ServiceCallException;
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
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private static final String INTERNSHIP = "Internship";
    private static final String NOT_EXISTING_IDS_ENDPOINT = "/users/not-existing-ids";

    @Value("${services.user-service.host}:${services.user-service.port}${spring.mvc.servlet.path}")
    private String baseUserServiceUrl;

    private final InternshipRepository internshipRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final RestTemplate restTemplate;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipDto createInternship(InternshipCreationDto creationDto) {
        log.info("Received request to create internship for project ID {}", creationDto.getProjectId());

        List<Long> allDtoUserIds = getAllCreationDtoUserIds(creationDto);
        TeamMember mentor = teamMemberService.findByUserIdAndProjectId(creationDto.getMentorUserId(), creationDto.getProjectId());
        Project project = projectService.getProjectById(creationDto.getProjectId());

        internshipValidator.validateNotExistingUserIds(getNotExistingUserIds(allDtoUserIds));
        internshipValidator.validateInternshipDuration(creationDto.getStartDate(), creationDto.getEndDate());
        internshipValidator.validateMentorRoles(mentor);

        Internship internship = internshipMapper.toEntity(creationDto);

        Team internTeam = new Team();
        internTeam.setProject(project);
        internTeam = teamService.save(internTeam);
        List<TeamMember> interns = createInterns(creationDto.getInternUserIds(), internTeam);

        internship.setInterns(interns);
        internship.setMentor(mentor);
        internship.setProject(project);
        internship.setStatus(InternshipStatus.NOT_STARTED);

        Internship savedInternship = internshipRepository.save(internship);
        log.info("Created internship with ID {} for project ID {}",
                savedInternship.getId(), creationDto.getProjectId()
        );

        return internshipMapper.toDto(savedInternship);
    }

    @Transactional
    public InternshipUpdateRequestDto updateInternship(InternshipUpdateDto updateDto) {
        log.info("Received a request to update internship status, internship ID {}", updateDto.getInternshipId());

        Internship internship = internshipRepository.findById(updateDto.getInternshipId())
                .orElseThrow(() -> new EntityNotFoundException(INTERNSHIP, updateDto.getInternshipId()));
        internshipValidator.validateInternshipStarted(internship);
        internshipValidator.validateInternshipIncomplete(internship);

        List<TeamMember> interns = internship.getInterns();
        List<Task> tasks = internship.getProject().getTasks();
        List<TeamMember> completedInterns = getCompletedInterns(interns, tasks);
        List<TeamMember> incompleteInterns = interns.stream()
                .filter(intern -> !completedInterns.contains(intern))
                .toList();

        updateInternsRoles(completedInterns, updateDto.getInternNewTeamRole());
        updateInternshipInfo(internship, completedInterns);
        if (LocalDateTime.now().isAfter(internship.getEndDate())) {
            teamMemberService.deleteAll(incompleteInterns);
        }

        internshipRepository.save(internship);

        log.info("The status of an internship with ID {} and the project team were updated.", updateDto.getInternshipId());
        return InternshipUpdateRequestDto.builder()
                .id(internship.getId())
                .completedInternUserIds(getTeamMembersIds(completedInterns))
                .incompleteInternUserIds(getTeamMembersIds(incompleteInterns))
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
                ).collect(Collectors.toList());

        log.info("Filtered internships: total={}, matching criteria={}", allInternships.size(), filteredInternships.size());
        return internshipMapper.toDto(filteredInternships);
    }

    public List<InternshipDto> getAllInternships() {
        log.info("Received request to get all internships.");
        return internshipMapper.toDto(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        log.info("Received request to get internship by its ID: {}.", internshipId);
        return internshipMapper.toDto(internshipRepository.findById(internshipId)
                        .orElseThrow(() -> new EntityNotFoundException(INTERNSHIP, internshipId)));
    }

    public void removeInternsFromInternship(long internshipId, List<Long> internUserIdsToRemove) {
        log.info("Received a request to remove interns with IDs: {} from the internship with ID {}",
                internUserIdsToRemove, internshipId);

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException(INTERNSHIP, internshipId));
        List<TeamMember> interns = internship.getInterns();
        internshipValidator.validateExistingInterns(internshipId, interns, internUserIdsToRemove);
        internshipValidator.validateInternshipIncomplete(internship);

        List<TeamMember> internsToRemove = interns.stream()
                .filter(intern -> internUserIdsToRemove.contains(intern.getUserId()))
                .toList();

        interns.removeAll(internsToRemove);
        teamMemberService.deleteAll(internsToRemove);

        internshipRepository.save(internship);

        log.info("Interns with user IDs {} were removed from the internship with ID {}",internUserIdsToRemove, internshipId);
    }

    private List<Long> getAllCreationDtoUserIds(InternshipCreationDto creationDto) {
        return Stream.concat(
                creationDto.getInternUserIds().stream(),
                Stream.of(creationDto.getCreatorUserId(), creationDto.getMentorUserId())
        ).toList();
    }

    private List<TeamMember> getCompletedInterns(List<TeamMember> interns, List<Task> tasks) {
        return interns.stream()
                .filter(intern -> tasks.stream()
                        .filter(task -> task.getPerformerUserId().equals(intern.getUserId()))
                        .allMatch(task -> task.getStatus().equals(TaskStatus.DONE))
                )
                .toList();
    }

    private List<TeamMember> createInterns(List<Long> userIds, Team team) {
        return userIds.stream()
                .map(userId -> teamMemberService.save(
                        TeamMember.builder()
                                .userId(userId)
                                .roles(List.of(TeamRole.INTERN))
                                .team(team)
                                .build()
                )).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Long> getTeamMembersIds(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .toList();
    }

    private void updateInternsRoles(List<TeamMember> completedInterns, TeamRole newTeamRole) {
        completedInterns.forEach(intern ->
                intern.getRoles().replaceAll(role -> role == TeamRole.INTERN ? newTeamRole : role));
        teamMemberService.saveAll(completedInterns);
    }

    private void updateInternshipInfo(Internship internship, List<TeamMember> updatedInterns) {
        if (LocalDateTime.now().isAfter(internship.getEndDate())) {
            internship.setStatus(InternshipStatus.COMPLETED);
            internship.setInterns(updatedInterns);
        } else {
            internship.setStatus(InternshipStatus.IN_PROGRESS);
        }
    }

    private List<Long> getNotExistingUserIds(List<Long> userIds) {
        String url = String.format("%s%s", baseUserServiceUrl, NOT_EXISTING_IDS_ENDPOINT);

        UserIdsDto requestDto = new UserIdsDto();
        requestDto.setUserIds(userIds);

        RequestEntity<UserIdsDto> request = RequestEntity
                .post(URI.create(url))
                .body(requestDto);

        try {
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Failed to call User Service for IDs {}: {}", userIds, e.getMessage());
            throw new ServiceCallException("An error occurred when requesting an external User Service!", e);
        }
    }
}