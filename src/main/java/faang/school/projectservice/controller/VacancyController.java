package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
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
    private final VacancyService vacancyService;

    public VacancyDto createVacancy(@NotNull @Valid VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    public VacancyDto updateVacancy(@NotNull @Valid VacancyDto vacancyDto) {
        checkDtoId(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    public void deleteVacancy(@NotNull @Valid VacancyDto vacancyDto) {
        checkDtoId(vacancyDto);
        vacancyService.deleteVacancy(vacancyDto);
    }

    public List<VacancyDto> getFilteredVacancies(@NotBlank String name, @NotBlank String position) {
        return vacancyService.getFilteredVacancies(name, position);
    }

    public VacancyDto getVacancy(@Positive long vacancyId) {
        return vacancyService.getVacancy(vacancyId);
    }

    private void checkDtoId(VacancyDto vacancyDto) {
        if (vacancyDto.id() == null || vacancyDto.id() < 0) {
            throw new DataValidationException("Vacancy ID cannot be null");
        }
    }
}
