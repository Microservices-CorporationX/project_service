package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private Candidate candidate;

    @BeforeEach
    public void setUp() {
        vacancyController = new VacancyController(vacancyService);
        candidate = new Candidate();
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullCandidate() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(null));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullName() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullDescription() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullProjectId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .count(1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeProjectId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .projectId(-1L)
                .count(1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeCount() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .projectId(1L)
                .count(-1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullCreatedBy() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeCreatedBy() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(-1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenDeletingVacancyWithNegativeId() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.deleteVacancy(-1));
    }

    @Test
    public void testIfThrowsExceptionWhenGettingFilteredVacanciesWithEmptyName() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.getFilteredVacancies(" ", "position"));
    }

    @Test
    public void testIfThrowsExceptionWhenGettingFilteredVacanciesWithEmptyPosition() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.getFilteredVacancies("name", " "));
    }

    @Test
    public void testIfThrowsExceptionWhenUpdatingVacancyWithNullCandidate() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.updateVacancy(1, null));
    }

    @Test
    public void testIfThrowsExceptionWhenUpdatingVacancyWithNegativeId() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.updateVacancy(-1, candidate));
    }

    @Test
    public void testIfThrowsExceptionWhenVacancyIdIsNegative() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.getVacancy(-1));
    }
}
