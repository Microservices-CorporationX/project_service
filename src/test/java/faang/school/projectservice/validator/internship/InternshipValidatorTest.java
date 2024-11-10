package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipCreationDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipValidatorTest {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private InternshipValidator validator;


    @Test
    void validateCreationDtoNotExistingUserTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        List<Long> missingUserIds = List.of(1L);

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .build();
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(missingUserIds));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("Not all user ids exist in database! Missing IDs: %s".formatted(missingUserIds), exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoExceededDurationTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        List<Long> missingUserIds = List.of();
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1 + MAX_INTERNSHIP_MONTHS_DURATION);

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(missingUserIds));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The internship should last no more than %d months!".formatted(MAX_INTERNSHIP_MONTHS_DURATION), exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoNotExistingProjectTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        List<Long> missingUserIds = List.of();
        long projectId = 10L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION);

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .projectId(projectId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(projectService.isProjectExists(projectId)).thenReturn(false);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(missingUserIds));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The project with ID %d does not exist in database!".formatted(projectId), exception.getMessage());
        verify(projectService, times(1)).isProjectExists(projectId);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoMentorIsNotOnProjectTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        List<Long> missingUserIds = List.of();
        long mentorUserId = 8L;
        long projectId = 10L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION);

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .mentorUserId(mentorUserId)
                .projectId(projectId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(projectService.isProjectExists(projectId)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(mentorUserId, projectId)).thenReturn(null);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(missingUserIds));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals(
                "There is no mentor with user ID %d working with a project with ID %d in the database."
                        .formatted(mentorUserId, projectId),
                exception.getMessage()
        );
        verify(projectService, times(1)).isProjectExists(projectId);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(mentorUserId, projectId);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoMentorIsInternTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        long mentorUserId = 8L;
        long projectId = 10L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION);

        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(TeamRole.INTERN));

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .mentorUserId(mentorUserId)
                .projectId(projectId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(projectService.isProjectExists(projectId)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(mentorUserId, projectId)).thenReturn(mentor);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The mentor can't be intern.", exception.getMessage());
        verify(projectService, times(1)).isProjectExists(projectId);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(mentorUserId, projectId);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoValidTest() {
        List<Long> internUserIds = List.of(1L, 2L, 3L, 4L);
        long mentorUserId = 8L;
        long projectId = 10L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION);
        TeamRole mentorTeamRole = TeamRole.ANALYST;

        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(internUserIds)
                .mentorUserId(mentorUserId)
                .projectId(projectId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        TeamMember mentorFromDb = TeamMember.builder()
                .userId(mentorUserId)
                .roles(List.of(mentorTeamRole))
                .build();

        when(projectService.isProjectExists(projectId)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(mentorUserId, projectId)).thenReturn(mentorFromDb);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        TeamMember mentor = assertDoesNotThrow(() -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals(mentorUserId, mentor.getUserId());
        verify(projectService, times(1)).isProjectExists(projectId);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(mentorUserId, projectId);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateUpdateDtoNotExistingInternshipTest() {
        Long internshipId = 9L;
        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(internshipId)
                .build();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(
                "There is no internship with ID (%d) in the database!".formatted(internshipId),
                exception.getMessage()
        );
        verify(internshipRepository, times(1)).findById(internshipId);
    }

    @Test
    void validateUpdateDtoCompletedInternshipTest() {
        Long internshipId = 9L;
        InternshipStatus internshipStatus = InternshipStatus.COMPLETED;

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(internshipStatus);

        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(internshipId)
                .build();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has been already completed!".formatted(internshipId), exception.getMessage());
        verify(internshipRepository, times(1)).findById(internshipId);
    }

    @Test
    void validateUpdateDtoNotStartedInternshipTest() {
        Long internshipId = 9L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        InternshipStatus internshipStatus = InternshipStatus.NOT_STARTED;

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(internshipStatus);
        internship.setStartDate(startDate);

        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(internshipId)
                .build();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has not started yet!".formatted(internshipId), exception.getMessage());
        verify(internshipRepository, times(1)).findById(internshipId);
    }

    @Test
    void validateUpdateDtoInternshipInProgressTest() {
        Long internshipId = 9L;
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        InternshipStatus internshipStatus = InternshipStatus.IN_PROGRESS;
        TeamRole internNewTeamRole = TeamRole.ANALYST;

        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(internshipStatus);
        internship.setStartDate(startDate);

        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(internshipId)
                .internNewTeamRole(internNewTeamRole)
                .build();

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        Internship internshipToUpdate = assertDoesNotThrow(() -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(internshipId, internshipToUpdate.getId());
        assertNotEquals(InternshipStatus.COMPLETED, internshipToUpdate.getStatus());
        verify(internshipRepository, times(1)).findById(internshipId);
    }

    @Test
    void validateInternsRemovalNotExistingInternshipTest() {
        Long internshipId = 9L;

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateInternsRemoval(internshipId, List.of()));
        assertEquals("There is no internship with ID (%d) in the database!".formatted(internshipId), exception.getMessage());
    }

    @Test
    void validateInternsRemovalNotExistingInternTest() {
        Long internshipId = 9L;
        Long notExistingInternUserId = 4L;
        Internship internship = new Internship();
        List<TeamMember> interns = List.of(
                TeamMember.builder().userId(1L).build(),
                TeamMember.builder().userId(2L).build(),
                TeamMember.builder().userId(3L).build()
        );
        internship.setInterns(interns);
        List<Long> internUserIdsToRemove = List.of(2L, notExistingInternUserId);

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateInternsRemoval(internshipId, internUserIdsToRemove));
        assertEquals("Intern with user ID %d is not part of the internship with ID %d."
                .formatted(notExistingInternUserId, internshipId), exception.getMessage());
    }
    @Test
    void validateInternsRemovalValidTest() {
        long internshipId = 9L;
        Internship internship = new Internship();
        List<TeamMember> interns = List.of(
                TeamMember.builder().userId(1L).build(),
                TeamMember.builder().userId(2L).build(),
                TeamMember.builder().userId(3L).build()
        );
        internship.setInterns(interns);
        List<Long> internUserIdsToRemove = List.of(2L, 3L);

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        assertDoesNotThrow(() -> validator.validateInternsRemoval(internshipId, internUserIdsToRemove));
    }
}