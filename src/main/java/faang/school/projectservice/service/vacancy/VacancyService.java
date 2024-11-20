package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;

import java.util.List;

public interface VacancyService {
    VacancyDto createVacancy(VacancyDto vacancyDto);
    VacancyDto updateVacancy(VacancyDto vacancyDto);
    void deleteVacancy(Long vacancyId);
    List<VacancyDto> getVacancies(VacancyFilterDto filters);
    VacancyDto getVacancy(Long id);
}
