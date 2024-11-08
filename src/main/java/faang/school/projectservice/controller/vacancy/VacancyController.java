package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.client.vacancy.VacancyDto;
import faang.school.projectservice.dto.client.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/new-vacancy")
    public VacancyDto createVacancy(@RequestBody @Validated(VacancyDto.Before.class) VacancyDto vacancyDto){
        log.info("New request to create a vacancy with id: {} and title: {} ", vacancyDto.id(), vacancyDto.description());
        return vacancyService.createVacancy(vacancyDto);
    }

    @GetMapping("/get-vacancy/{vacancyId}")
    public VacancyDto getVacancy(@PathVariable Long vacancyId){
        log.info("Received a request to get a vacancy with id: {}", vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }

    @PatchMapping("/update-vacancy")
    public VacancyDto updateVacancy(@RequestBody @Validated(VacancyDto.Before.class) VacancyDto vacancyDto){
        log.info("New request to update a vacancy with id: {} and title: {} ", vacancyDto.id(), vacancyDto.description());
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/delete-vacancy/{vacancyId}")
    public void deleteVacancy(@PathVariable @NotNull Long vacancyId){
        log.info("New request to delete a vacancy with id: {}", vacancyId);
        vacancyService.deleteVacancy(vacancyId);
        log.info("Vacancy with id: {} was successfully deleted", vacancyId);
    }

    @GetMapping("/projects/{projectId}/filter-vacancies")
    public List<VacancyDto> getFilteredVacancies(@PathVariable Long projectId, @RequestBody @Valid VacancyFilterDto filters){
        log.info("New request to get a vacancy with using filters: {} and {} ", filters.name(), filters.description());
        return vacancyService.getFilteredVacancies(projectId, filters);
    }
}
