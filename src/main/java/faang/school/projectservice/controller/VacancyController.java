package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public ResponseEntity<VacancyDto> createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(vacancyDto));
    }

    @PutMapping
    public ResponseEntity<VacancyDto> updateVacancyStatus(@Valid @RequestBody VacancyDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.updateVacancyStatus(dto));
    }
}
