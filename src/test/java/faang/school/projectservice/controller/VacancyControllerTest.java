package faang.school.projectservice.controller;

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

    private Project project;
    private Candidate candidate;

    @BeforeEach
    public void setUp() {
        vacancyController = new VacancyController(vacancyService);
        project = new Project();
        candidate = new Candidate();
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeCount() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", "description", project, -1, 1L, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeId() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", "description", project, 1, -1L, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithEmptyName() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(" ", "description", project, 1, 1L, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithEmptyDescription() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", " ", project, 1, 1L, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullProject() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", "description", null, 1, 1L, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullCuratorId() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", "description", project, 1, null, 1000.0));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNegativeSalary() {
        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy("name", "description", project, 1, 1L, -1000.0));
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
