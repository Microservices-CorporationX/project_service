package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @Test
    void createNullVacancy() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyController.createVacancy(null));
        assertEquals("Vacancy cannot be null", exception.getMessage());
    }

    @Test
    void createVacancy() {
        vacancyController.createVacancy(Mockito.mock(VacancyDto.class));
        Mockito.verify(vacancyService).createVacancy(Mockito.any(VacancyDto.class));
    }

    @Test
    void updateNullVacancy() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyController.updateVacancy(null));
        assertEquals("Vacancy cannot be null", exception.getMessage());
    }

    @Test
    void updateVacancy() {
        vacancyController.updateVacancy(Mockito.mock(VacancyDto.class));
        Mockito.verify(vacancyService).updateVacancy(Mockito.any(VacancyDto.class));
    }

    @Test
    void deleteNullVacancyId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyController.deleteVacancy(null));
        assertEquals("Vacancy Id cannot be null", exception.getMessage());
    }

    @Test
    void deleteVacancy() {
        vacancyController.deleteVacancy(Mockito.anyLong());
        Mockito.verify(vacancyService).deleteVacancy(Mockito.anyLong());
    }

    @Test
    void getVacancies() {
        vacancyController.getVacancies(Mockito.mock(VacancyFilterDto.class));
        Mockito.verify(vacancyService).getVacancies(Mockito.any(VacancyFilterDto.class));
    }

    @Test
    void getNullVacancyId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> vacancyController.getVacancy(null));
        assertEquals("Vacancy Id cannot be null", exception.getMessage());
    }

    @Test
    void getVacancy() {
        vacancyController.getVacancy(Mockito.anyLong());
        Mockito.verify(vacancyService).getVacancy(Mockito.anyLong());
    }
}