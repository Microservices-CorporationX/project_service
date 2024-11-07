package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/filters")
    public List<VacancyDto> getVacancyIdsByFilters(@RequestBody VacancyFilterDto filterDto) {
        return vacancyService.getVacancyIdsByFilters(filterDto);
    }

    @PostMapping("/create")
    public VacancyDto createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping("/update")
    public VacancyDto updateVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    @PostMapping("/close")
    public void closeVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        vacancyService.closeVacancy(vacancyDto);
    }
}