package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.impl.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public VacancyDto create(@Validated @RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }

    @PutMapping("/update")
    public VacancyDto updateVacancy(@Validated @RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping
    public void deleteVacancy(@Validated @RequestBody VacancyDto vacancyDto) {
        vacancyService.deleteVacancy(vacancyDto);
    }

    @GetMapping
    public List<VacancyDto> vacancyFilter(VacancyDtoFilter vacancyDtoFilter) {
        return vacancyService.vacancyFilter(vacancyDtoFilter);
    }

    @GetMapping("/{id}")
    public VacancyDto getVacancyById(@PathVariable ("id") Long id) {
        return vacancyService.getVacancyById(id);
    }
}