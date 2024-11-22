package faang.school.projectservice.service.impl.vacancy;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import java.util.List;

public interface VacancyService {
    VacancyDto create(VacancyDto vacancyDto);
    VacancyDto updateVacancy(VacancyDto vacancyDto);
    void deleteVacancy(VacancyDto vacancyDto);
    List<VacancyDto> vacancyFilter(VacancyDtoFilter vacancyDtoFilter);
    VacancyDto getVacancyById(Long id);
}