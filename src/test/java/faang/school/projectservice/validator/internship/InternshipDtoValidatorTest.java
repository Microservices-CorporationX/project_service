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
    private static final List<Long> DEFAULT_INTERN_USER_IDS = List.of(1L, 2L, 3L, 4L);
    private static final Long DEFAULT_MENTOR_USER_ID = 8L;
    private static final Long DEFAULT_PROJECT_ID = 10L;
    private static final LocalDateTime DEFAULT_START_DATE = LocalDateTime.now().plusMonths(1);
    private static final LocalDateTime DEFAULT_END_DATE = LocalDateTime.now().plusMonths(1 + MAX_INTERNSHIP_MONTHS_DURATION);
    private static final Long DEFAULT_INTERNSHIP_ID = 9L;

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
        prepareRestTemplate(List.of(1L));
        InternshipCreationDto creationDto = createDefaultCreationDto();

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("Not all user ids exist in database! Missing IDs: [1]", exception.getMessage());
        verifyRestTemplateCalledOnce();
    }

    @Test
    void validateCreationDtoExceededDurationTest() {
        prepareRestTemplate(List.of());
        InternshipCreationDto creationDto = createDefaultCreationDto();
        creationDto.setEndDate(LocalDateTime.now().plusMonths(4).plusDays(5));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The internship should last no more than %d months!".formatted(MAX_INTERNSHIP_MONTHS_DURATION), exception.getMessage());
        verifyRestTemplateCalledOnce();
    }

    @Test
    void validateCreationDtoNotExistingProjectTest() {
        prepareRestTemplate(List.of());
        InternshipCreationDto creationDto = createDefaultCreationDto();
        when(projectService.isProjectExists(DEFAULT_PROJECT_ID)).thenReturn(false);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The project with ID %d does not exist in database!".formatted(DEFAULT_PROJECT_ID), exception.getMessage());
        verifyRestTemplateCalledOnce();
        verify(projectService, times(1)).isProjectExists(DEFAULT_PROJECT_ID);
    }

    @Test
    void validateCreationDtoMentorIsNotOnProjectTest() {
        prepareRestTemplate(List.of());
        InternshipCreationDto creationDto = createDefaultCreationDto();
        when(projectService.isProjectExists(DEFAULT_PROJECT_ID)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID)).thenReturn(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals(
                "There is no mentor with user ID %d working with a project with ID %d in the database."
                        .formatted(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID),
                exception.getMessage()
        );
        verifyRestTemplateCalledOnce();
        verify(projectService, times(1)).isProjectExists(DEFAULT_PROJECT_ID);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID);
    }

    @Test
    void validateCreationDtoMentorIsInternTest() {
        TeamMember teamMember = new TeamMember();
        teamMember.setRoles(List.of(TeamRole.INTERN));

        prepareRestTemplate(List.of());
        InternshipCreationDto creationDto = createDefaultCreationDto();
        when(projectService.isProjectExists(DEFAULT_PROJECT_ID)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID)).thenReturn(teamMember);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateCreationDtoAndGetMentor(creationDto));

        assertEquals("The mentor can't be intern.", exception.getMessage());
        verifyRestTemplateCalledOnce();
        verify(projectService, times(1)).isProjectExists(DEFAULT_PROJECT_ID);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID);
    }

    @Test
    void validateCreationDtoValidTest() {
        TeamMember teamMember = TeamMember.builder()
                .userId(DEFAULT_MENTOR_USER_ID)
                .roles(List.of(TeamRole.ANALYST))
                .build();

        prepareRestTemplate(List.of());
        InternshipCreationDto creationDto = createDefaultCreationDto();
        when(projectService.isProjectExists(DEFAULT_PROJECT_ID)).thenReturn(true);
        when(teamMemberService.getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID)).thenReturn(teamMember);

        TeamMember mentor = validator.validateCreationDtoAndGetMentor(creationDto);

        assertEquals(DEFAULT_MENTOR_USER_ID, mentor.getUserId());
        verifyRestTemplateCalledOnce();
        verify(projectService, times(1)).isProjectExists(DEFAULT_PROJECT_ID);
        verify(teamMemberService, times(1)).getByUserIdAndProjectId(DEFAULT_MENTOR_USER_ID, DEFAULT_PROJECT_ID);
    }

    @Test
    void validateUpdateDtoNotExistingInternshipTest() {
        InternshipUpdateDto updateDto = createDefaultUpdateDto();
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(
                "There is no internship with ID (%d) in the database!".formatted(DEFAULT_INTERNSHIP_ID),
                exception.getMessage()
        );
        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
    }

    @Test
    void validateUpdateDtoCompletedInternshipTest() {
        Internship internship = createDefaultInternship();
        internship.setStatus(InternshipStatus.COMPLETED);

        InternshipUpdateDto updateDto = createDefaultUpdateDto();
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has been already completed!".formatted(DEFAULT_INTERNSHIP_ID), exception.getMessage());
        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
    }

    @Test
    void validateUpdateDtoNotStartedInternshipTest() {
        Internship internship = createDefaultInternship();
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(DEFAULT_START_DATE);

        InternshipUpdateDto updateDto = createDefaultUpdateDto();
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.of(internship));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals("The internship with ID (%d) has not started yet!".formatted(DEFAULT_INTERNSHIP_ID), exception.getMessage());
        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
    }

    @Test
    void validateUpdateDtoValidTest() {
        Internship internship = createDefaultInternship();
        InternshipUpdateDto updateDto = createDefaultUpdateDto();
        when(internshipRepository.findById(DEFAULT_INTERNSHIP_ID)).thenReturn(Optional.of(internship));

        Internship internshipToUpdate = assertDoesNotThrow(() -> validator.validateUpdateDtoAndGetInternship(updateDto));

        assertEquals(DEFAULT_INTERNSHIP_ID, internshipToUpdate.getId());
        assertNotEquals(internshipToUpdate.getStatus(), InternshipStatus.COMPLETED);
        verify(internshipRepository, times(1)).findById(DEFAULT_INTERNSHIP_ID);
    }

    private Internship createDefaultInternship() {
        Internship internship = new Internship();
        internship.setId(DEFAULT_INTERNSHIP_ID);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setStartDate(LocalDateTime.now().minusMonths(1));
        return internship;
    }

    private InternshipCreationDto createDefaultCreationDto() {
        return InternshipCreationDto.builder()
                .internUserIds(DEFAULT_INTERN_USER_IDS)
                .mentorUserId(DEFAULT_MENTOR_USER_ID)
                .projectId(DEFAULT_PROJECT_ID)
                .startDate(DEFAULT_START_DATE)
                .endDate(DEFAULT_END_DATE)
                .build();
    }

    private InternshipUpdateDto createDefaultUpdateDto() {
        return InternshipUpdateDto.builder()
                .internshipId(DEFAULT_INTERNSHIP_ID)
                .internNewTeamRole(TeamRole.ANALYST)
                .build();
    }

    private void prepareRestTemplate(List<Long> notExistingUsers) {
        when(restTemplate.exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        )).thenReturn(ResponseEntity.ok(notExistingUsers));
    }

    private void verifyRestTemplateCalledOnce() {
        verify(restTemplate, times(1)).exchange(
                any(RequestEntity.class),
                eq(new ParameterizedTypeReference<List<Long>>() {})
        );
    }
}