package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    public void createVacancy(@RequestBody VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        validateNameVacancy(vacancyDto.getName());
        vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto) {
        validateVacancy(vacancyDto);
        validateNameVacancy(vacancyDto.getName());
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    public void deleteVacancy(@PathVariable Long vacancyId) {
        validateId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    @PostMapping("/filter")
    public List<VacancyDto> getVacanciesByFilter(@RequestBody VacancyFilterDto filters) {
        return vacancyService.getVacanciesByFilter(filters);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancyById(Long vacancyId) {
        validateId(vacancyId);
        return vacancyService.findById(vacancyId);
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