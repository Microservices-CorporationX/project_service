package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.client.internship.InternshipCreationDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
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
class InternshipDtoValidatorTest {

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
    InternshipDtoValidator validator;


    @Test
    void validateCreationDtoNotExistingUserTest() {
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION))
                .build();
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of(1L)));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("Not all user ids exist in database! Missing IDs: [1]", exception.getMessage());
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoExceededDurationTest() {
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION).plusDays(5))
                .build();
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

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
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION))
                .build();
        when(projectService.isProjectExists(10L)).thenReturn(false);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The project with ID %d does not exist in database!".formatted(10L), exception.getMessage());
        verify(projectService, times(1)).isProjectExists(10L);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoMentorIsNotOnProjectTest() {
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION))
                .build();
        when(projectService.isProjectExists(10L)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(8L, 10L)).thenReturn(null);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals(
                "There is no mentor with user ID %d working with a project with ID %d in the database."
                        .formatted(8L, 10L),
                exception.getMessage()
        );
        verify(projectService, times(1)).isProjectExists(10L);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(8L, 10L);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoMentorIsInternTest() {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.INTERN));
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION))
                .build();
        when(projectService.isProjectExists(10L)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(8L, 10L)).thenReturn(teamMember);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The mentor can't be intern.", exception.getMessage());
        verify(projectService, times(1)).isProjectExists(10L);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(8L, 10L);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateCreationDtoValidTest() {
        TeamMember teamMember = TeamMember.builder()
                .userId(8L)
                .roles(List.of(TeamRole.ANALYST))
                .build();
        InternshipCreationDto creationDto = InternshipCreationDto.builder()
                .internUserIds(List.of(1L, 2L, 3L, 4L))
                .mentorUserId(8L)
                .projectId(10L)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusMonths(MAX_INTERNSHIP_MONTHS_DURATION))
                .build();
        when(projectService.isProjectExists(10L)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(8L, 10L)).thenReturn(teamMember);
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(List.of()));

        TeamMember mentor = assertDoesNotThrow(() -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals(8L, mentor.getUserId());
        verify(projectService, times(1)).isProjectExists(10L);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(8L, 10L);
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }

    @Test
    void validateUpdateDtoNotExistingInternshipTest() {
        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(9L)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
        when(internshipRepository.findById(9L)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(
                "There is no internship with ID (%d) in the database!".formatted(9L),
                exception.getMessage()
        );
        verify(internshipRepository, times(1)).findById(9L);
    }

    @Test
    void validateUpdateDtoCompletedInternshipTest() {
        Internship internship = new Internship();
        internship.setId(9L);
        internship.setStartDate(LocalDateTime.now().minusMonths(1));
        internship.setStatus(InternshipStatus.COMPLETED);
        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(9L)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
        when(internshipRepository.findById(9L)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has been already completed!".formatted(9L), exception.getMessage());
        verify(internshipRepository, times(1)).findById(9L);
    }

    @Test
    void validateUpdateDtoNotStartedInternshipTest() {
        Internship internship = new Internship();
        internship.setId(9L);
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(LocalDateTime.now().plusDays(1));
        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(9L)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
        when(internshipRepository.findById(9L)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has not started yet!".formatted(9L), exception.getMessage());
        verify(internshipRepository, times(1)).findById(9L);
    }

    @Test
    void validateUpdateDtoValidTest() {
        Internship internship = new Internship();
        internship.setId(9L);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setStartDate(LocalDateTime.now().minusMonths(1));
        InternshipUpdateDto updateDto = InternshipUpdateDto.builder()
                .internshipId(9L)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
        when(internshipRepository.findById(9L)).thenReturn(Optional.of(internship));

        Internship internshipToUpdate = assertDoesNotThrow(() -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(9L, internshipToUpdate.getId());
        assertNotEquals(InternshipStatus.COMPLETED, internshipToUpdate.getStatus());
        verify(internshipRepository, times(1)).findById(9L);
    }
}