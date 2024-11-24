package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.service.VacationService;
import faang.school.projectservice.utilities.UrlUtils;
import faang.school.projectservice.validation.ValidationVacancies;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
public class VacancyController {
    private final VacationService vacationService;
    private final ValidationVacancies validationVacancies;

    @PostMapping()
    public VacancyDto save(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.hasToBeNull(vacancyDto.id(), "id vacancy");
        validationVacancies.hasToBeNull(vacancyDto.candidates(), "candidates");
        validationVacancies.hasToBeNotNull(vacancyDto.name(), "name");
        validationVacancies.hasToBeNotNull(vacancyDto.description(), "description");
        validationVacancies.hasToBeNotNull(vacancyDto.projectId(), "projectId");
        validationVacancies.hasToBeNotNull(vacancyDto.createdBy(), "createdBy");
        validationVacancies.hasToBeNotNull(vacancyDto.updatedBy(), "updatedBy");
        validationVacancies.hasToBeNotNull(vacancyDto.count(), "count");

        VacancyDto vacancyDtoResult = vacationService.saveVacation(vacancyDto);

        log.info("VacancyController.save - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @PatchMapping()
    public VacancyDto update(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.hasToBeNotNull(vacancyDto.id(), "id vacancy");
        validationVacancies.hasToBeNotNull(vacancyDto.updatedBy(), "updatedBy");
        validationVacancies.hasToBeNotNull(vacancyDto.projectId(), "projectId");
        validationVacancies.hasToBeNull(vacancyDto.candidates(), "candidates");

        VacancyDto vacancyDtoResult = vacationService.updateVacation(vacancyDto);

        log.info("VacancyController.update - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @DeleteMapping()
    public VacancyDto delete(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.hasToBeNotNull(vacancyDto.id(), "id vacancy");
        validationVacancies.isVacancyExist(vacancyDto.id());

        VacancyDto vacancyDtoResult = vacationService.deleteVacation(vacancyDto.id());

        log.info("VacancyController.delete - id: {}", vacancyDtoResult.id());
        return vacancyDtoResult;
    }

    @PostMapping(UrlUtils.ID)
    public VacancyDto getVacancyById(@NotNull @RequestBody VacancyDto vacancyDto) {
        validationVacancies.hasToBeNotNull(vacancyDto.id(), "id vacancy");
        VacancyDto vacancyDtoResult = vacationService.getVacancyById(vacancyDto.id());

        log.info("VacancyController.getVacancyById - id: {}", vacancyDto.id());
        return vacancyDtoResult;
    }

    @GetMapping(UrlUtils.VACANCY_FILTER)
    public List<VacancyDto> filter(@RequestBody FilterVacancyDto filterVacancyDto) {
        List<VacancyDto> vacancyDtoList = vacationService.findAll(filterVacancyDto);

        log.info("VacancyController.filter - 1) filter: {}; 2) send number items: {}",
                filterVacancyDto.toString(),
                vacancyDtoList == null ? 0 : vacancyDtoList.size());
        return vacancyDtoList;
    }
}