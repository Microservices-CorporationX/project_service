package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        validateValidateVacancy(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        validateValidateVacancy(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(Long vacancyId) {
        validateVacancyId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    public List<VacancyDto> getVacancies(VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancies(vacancyFilterDto);
    }

    public VacancyDto getVacancy(Long vacancyId) {
        validateVacancyId(vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }

    private void validateValidateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new IllegalArgumentException("Vacancy cannot be null");
        }
    }

    private void validateVacancyId(Long vacancyId) {
        if (vacancyId == null) {
            throw new IllegalArgumentException("Vacancy Id cannot be null");
        }
    }
}
