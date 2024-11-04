package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class VacancyController {
    private final VacancyValidator vacancyValidator = new VacancyValidator();
    private final VacancyService vacancyService;

    public void createVacancy(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new DataValidationException("Vacancy cannot be null");
        }
        vacancyValidator.validateVacancyFields(vacancyDto);
        vacancyService.createVacancy(vacancyDto);
    }

    public void updateVacancy(long vacancyId, Candidate candidate) {
        vacancyValidator.validateVacancyId(vacancyId);
        vacancyValidator.validateCandidate(candidate);
        vacancyService.updateVacancy(vacancyId, candidate);
    }

    public void deleteVacancy(long vacancyId) {
        vacancyValidator.validateVacancyId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    public List<VacancyDto> getFilteredVacancies(String name, String position) {
        vacancyValidator.validateFilters(name, position);
        return vacancyService.getFilteredVacancies(name, position);
    }

    public VacancyDto getVacancy(long vacancyId) {
        vacancyValidator.validateVacancyId(vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }
}
