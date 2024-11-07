package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.validator.VacancyValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Validated
public class VacancyController {
    private final VacancyValidator vacancyValidator = new VacancyValidator();
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(@NotNull VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyFields(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(@NotNull VacancyDto vacancyDto) {
        vacancyValidator.validateId(vacancyDto.id());
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(@NotNull VacancyDto vacancyDto) {
        vacancyValidator.validateId(vacancyDto.id());
        vacancyService.deleteVacancy(vacancyDto);
    }

    public List<VacancyDto> getFilteredVacancies(@NotBlank String name, @NotBlank String position) {
        return vacancyService.getFilteredVacancies(name, position);
    }

    public VacancyDto getVacancy(@Positive long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }
}
