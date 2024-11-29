package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyControllerValidator;
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
@RequestMapping("/api/v1/project/vacancy")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyControllerValidator validator;

    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto createVacancy(@RequestBody VacancyDto vacancyDto) {
        validator.validateVacancyDto(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto) {
        validator.validateVacancyDto(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    public void deleteVacancy(@PathVariable Long vacancyId) {
        validator.validateVacancyId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    @PostMapping("/filtered")
    public List<VacancyDto> getVacancies(@RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancies(vacancyFilterDto);
    }

    @GetMapping("/{vacancyId}")
    public VacancyDto getVacancy(@PathVariable Long vacancyId) {
        validator.validateVacancyId(vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }
}
