package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    public void createVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        validateNameVacancy(vacancyDto.getName());
        vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        validateNameVacancy(vacancyDto.getName());
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(Long id) {
        validateId(id);
        vacancyService.deleteVacancy(id);
    }

    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters) {
        return vacancyService.getVacanciesByFilter(filters);
    }

    public VacancyDto getVacancyById(Long id) {
        validateId(id);
        return vacancyService.findById(id);
    }

    private void validateVacancy(VacancyDto vacancyDto) {
        if (vacancyDto.getProjectId() == null) {
            throw new DataValidationException("Project ID is null");
        }

        if (vacancyDto.getCount() == null || vacancyDto.getCount() <= 0) {
            throw new DataValidationException("Count must be positive");
        }

        if (vacancyDto.getCreatedBy() == null) {
            throw new DataValidationException("Curator ID is null");
        }
    }

    private void validateId(Long id) {
        if (id < 0) {
            throw new DataValidationException("ID can not be negative");
        }
    }

    private void validateNameVacancy(String nameVacancy) {
        if (nameVacancy == null || nameVacancy.isBlank()) {
            throw new DataValidationException("The name can not be empty");
        }
    }
}