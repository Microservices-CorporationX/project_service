package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {
    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @BeforeEach
    public void setUp() {
        vacancyController = new VacancyController(vacancyService);
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullDto() {
        assertThrows(NullPointerException.class,
                () -> vacancyController.createVacancy(null));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        assertThrows(DataValidationException.class,
                () -> vacancyController.createVacancy(vacancyDto));
    }

    @Test
    public void testIfThrowsExceptionWhenCreatingVacancyWithNullName() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
                .id(1L)
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
    public void testIfCreatesVacancyWithValidDto() {
        // arrange
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        // act
        vacancyController.createVacancy(vacancyDto);

        // assert
        verify(vacancyService).createVacancy(vacancyDto);
    }

    @Test
    public void testIfThrowsExceptionWhenDeletingVacancyWithNegativeId() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(-1L)
                .build();


        assertThrows(DataValidationException.class,
                () -> vacancyController.deleteVacancy(vacancyDto));
    }

    @Test
    public void testIfDeletesVacancyWithValidDto() {
        // arrange
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .build();

        // act
        vacancyController.deleteVacancy(vacancyDto);

        // assert
        verify(vacancyService).deleteVacancy(vacancyDto);
    }

    @Test
    public void testIfGetsVacancyWithValidId() {
        // arrange
        Long id = 1L;

        // act
        vacancyController.getVacancy(id);

        // assert
        verify(vacancyService).getVacancy(id);
    }

    @Test
    public void testIfGetsFilteredVacanciesWithValidNameAndPosition() {
        // arrange
        String name = "name";
        String position = "position";

        // act
        vacancyController.getFilteredVacancies(name, position);

        // assert
        verify(vacancyService).getFilteredVacancies(name, position);
    }

    @Test
    public void testIfUpdatesVacancyWithValidDto() {
        // arrange
        VacancyDto vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .projectId(1L)
                .count(1)
                .createdBy(1L)
                .build();

        // act
        vacancyController.updateVacancy(vacancyDto);

        // assert
        verify(vacancyService).updateVacancy(vacancyDto);
    }
}