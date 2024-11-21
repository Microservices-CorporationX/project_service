package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyControllerValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VacancyControllerTest {

    @Mock
    private VacancyControllerValidator validator;

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    @Test
    void createVacancy(){
        vacancyController.createVacancy(Mockito.mock(VacancyDto.class));
        Mockito.verify(validator).validateVacancyDto(Mockito.any(VacancyDto.class));
        Mockito.verify(vacancyService).createVacancy(Mockito.any(VacancyDto.class));
    }

    @Test
    void updateVacancy(){
        vacancyController.updateVacancy(Mockito.mock(VacancyDto.class));
        Mockito.verify(validator).validateVacancyDto(Mockito.any(VacancyDto.class));
        Mockito.verify(vacancyService).updateVacancy(Mockito.any(VacancyDto.class));
    }

    @Test
    void deleteVacancy(){
        vacancyController.deleteVacancy(1L);
        Mockito.verify(validator).validateVacancyId(1L);
        Mockito.verify(vacancyService).deleteVacancy(1L);
    }

    @Test
    void getVacancies(){
        vacancyController.getVacancies(Mockito.mock(VacancyFilterDto.class));
        Mockito.verify(vacancyService).getVacancies(Mockito.any(VacancyFilterDto.class));
    }

    @Test
    void getVacancy(){
        vacancyController.getVacancy(1L);
        Mockito.verify(validator).validateVacancyId(1L);
        Mockito.verify(vacancyService).getVacancy(1L);
    }
}