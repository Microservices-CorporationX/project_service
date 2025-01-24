package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.utility.validator.VacancyDtoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("vacancy")
@RestController
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;
    private final VacancyDtoValidator vacancyDtoValidator;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<VacancyDto> createVacancy(@RequestBody VacancyDto vacancyDto) {
        vacancyDtoValidator.validate(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Long userId = userContext.getUserId();
        vacancy = vacancyService.createVacancy(vacancy, userId);
        return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
    }

    @PatchMapping("close/{id}")
    public ResponseEntity<VacancyDto> closeVacancy(@PathVariable("id") Long vacancyId) {
        Vacancy vacancy = vacancyService.closeVacancy(vacancyId, userContext.getUserId());
        VacancyDto vacancyDto = vacancyMapper.toDto(vacancy);
        return ResponseEntity.ok(vacancyDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable("id") Long vacancyId) {
        VacancyDto vacancyDto = vacancyMapper.toDto(vacancyService.getVacancy(vacancyId));
        return ResponseEntity.ok(vacancyDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<VacancyDto> updateVacancy(@PathVariable("id") Long vacancyId,
                                                    @RequestBody VacancyDto vacancyDto) {
        vacancyDtoValidator.validate(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        Long userId = userContext.getUserId();
        VacancyDto result = vacancyMapper.toDto(vacancyService.updateVacancy(vacancyId, vacancy, userId));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable("id") Long vacancyId) {
        Long userId = userContext.getUserId();
        vacancyService.deleteVacancy(vacancyId, userId);
        return ResponseEntity.ok().build();
    }
}
