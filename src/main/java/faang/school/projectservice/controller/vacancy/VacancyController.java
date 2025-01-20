package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.utility.validator.VacancyDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;
    private final VacancyDtoValidator vacancyDtoValidator;

    public ResponseEntity<VacancyDto> createVacancy(@Valid VacancyDto vacancyDto) {
        vacancyDtoValidator.validate(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy = vacancyService.createVacancy(vacancy);
        return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
    }
}
