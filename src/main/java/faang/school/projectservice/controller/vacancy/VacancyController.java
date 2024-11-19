package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.filter.VacancyDtoFilter;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.impl.vacancy.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RequiredArgsConstructor
@RestController
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping("/create")
    public VacancyDto create(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.create(vacancyDto);
    }
    @PostMapping("/update")
    public VacancyDto updateVacancy(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.updateVacancy(vacancyDto);
    }
    @PostMapping("/delete")
    public void deleteVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyService.deleteVacancy(vacancyDto);
    }
    @PostMapping("/filter")
    public List<VacancyDto> vacancyFilter(@RequestBody VacancyDtoFilter vacancyDtoFilter) {
        return vacancyService.vacancyFilter(vacancyDtoFilter);
    }
    @PostMapping("/getVac_id")
    public VacancyDto getVacancyForId(@RequestBody VacancyDto vacancyDto) {
        return vacancyService.getVacancyForId(vacancyDto);
    }
}