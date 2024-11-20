package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.NewVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Vacancy API", description = "Endpoints for vacancy logic in project service")
public class VacancyController {
    private final VacancyService vacancyService;

    @Operation(summary = "Create new vacancy")
    @PostMapping
    public ResponseEntity<VacancyResponseDto> createVacancy(@Valid @RequestBody NewVacancyDto dto) {
        log.info("Received request from user id {} to create new vacancy: {}", dto.getCreatedById(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(dto));
    }

    @Operation(summary = "Update vacancy status by id")
    @PutMapping
    public ResponseEntity<VacancyResponseDto> updateVacancyStatus(@Valid @RequestBody VacancyUpdateDto dto) {
        log.info("Received request for update from user id {} for vacancy: {}", dto.getUpdatedById(), dto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.updateVacancyStatus(dto));
    }

    @Operation(summary = "Delete vacancy by id")
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable @Positive long vacancyId) {
        log.info("Received request to delete vacancy {}", vacancyId);
        vacancyService.deleteVacancy(vacancyId);
        log.info("Vacancy {} deleted successfully", vacancyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Receive a list of vacancies filtered by a provided filter")
    @PostMapping("/filters/")
    public ResponseEntity<List<VacancyResponseDto>> filterVacancies(@Valid @RequestBody FilterVacancyDto filters) {
        log.info("Filters request received: {}", filters);
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.filterVacancies(filters));
    }

    @Operation(summary = "Get vacancy by its id")
    @GetMapping("/{vacancyId})")
    public ResponseEntity<VacancyResponseDto> getVacancy(@PathVariable @Positive long vacancyId) {
        log.info("Request to get vacancy #{} received", vacancyId);
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.getVacancyDtoById(vacancyId));
    }
}
