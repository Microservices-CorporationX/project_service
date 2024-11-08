package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancies")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;

    @PostMapping
    public ResponseEntity<VacancyDto> createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        log.info("Received request from user id {} to create new vacancy: {}", vacancyDto.getCreatedBy(), vacancyDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(vacancyDto));
    }

    @PutMapping
    public ResponseEntity<VacancyDto> updateVacancyStatus(@Valid @RequestBody VacancyDto dto) {
        log.info("Received request for update from user id {} for vacancy: {}", dto.getUpdatedBy(), dto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.updateVacancyStatus(dto));
    }

    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable @Positive long vacancyId) {
        log.info("Received request to delete vacancy {}", vacancyId);
        vacancyService.deleteVacancy(vacancyId);
        log.info("Vacancy {} deleted successfully", vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/filters/")
    public ResponseEntity<List<VacancyDto>> filterVacancies(@Valid @RequestBody FilterVacancyDto filters) {
        log.info("Filters request received: {}", filters);
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.filterVacancies(filters));
    }
}
