package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
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

    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable @Positive long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/filters/")
    public ResponseEntity<List<VacancyDto>> filterVacancies(@Valid @RequestBody FilterVacancyDto filters) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.filterVacancies(filters));
    }

    @GetMapping("/{vacancyId})")
    public ResponseEntity<VacancyDto> getVacancy(@PathVariable @Positive long vacancyId) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.getVacancyDtoById(vacancyId));
    }
}
