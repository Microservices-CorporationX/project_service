package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ServiceCallException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternshipDtoValidator {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;
    private static final  String USER_SERVICE_URL = "http://localhost:8080/api/v1/users";

    private final RestTemplate restTemplate;
    private final InternshipRepository internshipRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public TeamMember validateCreationDtoAndGetMentor(InternshipCreationDto creationDto) {
        List<Long> allDtoUsers =
                Stream.concat(
                        creationDto.getInternUserIds().stream(),
                        Stream.of(creationDto.getCreatorUserId(), creationDto.getMentorUserId())
                ).toList();

        validateUserIds(allDtoUsers);
        validateInternshipDuration(creationDto.getStartDate(), creationDto.getEndDate());
        validateProjectExistence(creationDto.getProjectId());

        return getMentorForProject(creationDto.getMentorUserId(), creationDto.getProjectId());
    }

    public Internship validateUpdateDtoAndGetInternship(InternshipUpdateDto updateDto) {
        Internship internship = internshipRepository.findById(updateDto.getInternshipId())
                .orElseThrow(
                        () -> new DataValidationException("There is no internship with ID (%d) in the database!"
                                .formatted(updateDto.getInternshipId())));

        validateInternshipStatus(internship, updateDto.getInternshipId());

        return internship;
    }

    private void validateInternshipStatus(Internship internship, long internshipId) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException(
                    "The internship with ID (%d) has been already completed!".formatted(internshipId)
            );
        }
        if (internship.getStatus() != InternshipStatus.IN_PROGRESS
                && LocalDateTime.now().isBefore(internship.getStartDate())) {
            throw new DataValidationException(
                    "The internship with ID (%d) has not started yet!".formatted(internshipId)
            );
        }
    }

    private void validateUserIds(List<Long> userIds) {
        List<Long> notExistingUserIds = getNotExistingUserIds(userIds);

        if (!notExistingUserIds.isEmpty()) {
            throw new DataValidationException(
                    String.format("Not all user ids exist in database! Missing IDs: %s", notExistingUserIds)
            );
        }
    }

    private void validateInternshipDuration(LocalDateTime startDate, LocalDateTime endDate) {
        Period internshipDuration = Period.between(startDate.toLocalDate(), endDate.toLocalDate());

        if (internshipDuration.getMonths() > MAX_INTERNSHIP_MONTHS_DURATION
                || internshipDuration.getMonths() == MAX_INTERNSHIP_MONTHS_DURATION && internshipDuration.getDays() > 0
        ) {
            throw new DataValidationException(
                    "The internship should last no more than %d months!".formatted(MAX_INTERNSHIP_MONTHS_DURATION)
            );
        }
    }

    private void validateProjectExistence(long projectId) {
        if (!projectService.isProjectExists(projectId)) {
            throw new DataValidationException("The project with ID %d does not exist in database!".formatted(projectId));
        }
    }

    private TeamMember getMentorForProject(long mentorUserId, long projectId) {
        TeamMember mentor = teamMemberService.getByUserIdAndProjectId(mentorUserId, projectId);
        if (mentor == null) {
            throw new DataValidationException(
                    "There is no mentor with user ID %d working with a project with ID %d in the database."
                            .formatted(mentorUserId, projectId));
        }
        if (mentor.getRoles().contains(TeamRole.INTERN)) {
            throw new DataValidationException("The mentor can't be intern.");
        }
        return mentor;
    }

    private List<Long> getNotExistingUserIds(List<Long> userIds) {
        String url = String.format("%s/not-existing-ids", USER_SERVICE_URL);

        RequestEntity<List<Long>> request = RequestEntity
                .post(URI.create(url))
                .body(userIds);

        try {
            ResponseEntity<List<Long>> response = restTemplate.exchange(
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("An error occurred when requesting an external User Service!", e);
            throw new ServiceCallException("An error occurred when requesting an external User Service!", e);
        }
    }
}
