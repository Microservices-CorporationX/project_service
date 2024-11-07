package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
@Validated
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/create")
    public VacancyDto createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.createVacancy(vacancyDto);
    }

    @PostMapping("/update")
    public VacancyDto updateVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    @PostMapping("/close")
    public void closeVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        vacancyService.closeVacancy(vacancyDto);
    }
}