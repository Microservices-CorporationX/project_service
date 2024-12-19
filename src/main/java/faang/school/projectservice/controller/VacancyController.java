package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import faang.school.projectservice.utilities.UrlUtils;
import faang.school.projectservice.validation.ValidationVacancies;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.VACANCY)
public class VacancyController {
    private final VacancyService vacancyService;
    private final ValidationVacancies validationVacancies;

    @PostMapping()
    public VacancyDto save(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.checkNull(vacancyDto.id(), "id vacancy");
        validationVacancies.checkNull(vacancyDto.candidates(), "candidates");
        validationVacancies.checkNotNull(vacancyDto.name(), "name");
        validationVacancies.checkNotNull(vacancyDto.description(), "description");
        validationVacancies.checkNotNull(vacancyDto.projectId(), "projectId");
        validationVacancies.checkNotNull(vacancyDto.createdBy(), "createdBy");
        validationVacancies.checkNotNull(vacancyDto.updatedBy(), "updatedBy");
        validationVacancies.checkNotNull(vacancyDto.count(), "count");

        VacancyDto vacancyDtoResult = vacancyService.saveVacancy(vacancyDto);

        log.info("VacancyController.save - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @PatchMapping()
    public VacancyDto update(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.checkNotNull(vacancyDto.id(), "id vacancy");
        validationVacancies.checkNotNull(vacancyDto.updatedBy(), "updatedBy");
        validationVacancies.checkNotNull(vacancyDto.projectId(), "projectId");
        validationVacancies.checkNull(vacancyDto.candidates(), "candidates");

        VacancyDto vacancyDtoResult = vacancyService.updateVacancy(vacancyDto);

        log.info("VacancyController.update - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @DeleteMapping()
    public VacancyDto delete(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.checkNotNull(vacancyDto.id(), "id vacancy");
        validationVacancies.vacancyExist(vacancyDto.id());

        VacancyDto vacancyDtoResult = vacancyService.deleteVacancy(vacancyDto.id());

        log.info("VacancyController.delete - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @PostMapping(UrlUtils.VACANCY_ID)
    public VacancyDto getVacancyById(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.checkNotNull(vacancyDto.id(), "id vacancy");
        VacancyDto vacancyDtoResult = vacancyService.getVacancyById(vacancyDto.id());

        log.info("VacancyController.getVacancyById - id: {}", vacancyDto.id());
        return vacancyDtoResult;
    }

    @PostMapping(UrlUtils.VACANCY_FILTER)
    public List<VacancyDto> filter(@RequestBody FilterVacancyDto filterVacancyDto) {
        List<VacancyDto> vacancyDtoList = vacancyService.findByFilter(filterVacancyDto);

        log.info("VacancyController.filter - 1) filter: {}; 2) send number items: {}",
                filterVacancyDto.toString(),
                vacancyDtoList == null ? 0 : vacancyDtoList.size());
        return vacancyDtoList;
    }
}