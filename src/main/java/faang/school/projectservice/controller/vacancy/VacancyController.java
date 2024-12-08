package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.dto.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.VacancyService;
import faang.school.projectservice.validator.vacancy.VacancyControllerValidator;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project/vacancy")
@Tag(name = "Контроллер для управления вакансиями")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyControllerValidator validator;

    private final VacancyService vacancyService;

    @PostMapping
    @Operation(summary = "Создать вакансию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vacancy created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VacancyDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Vacancy already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public VacancyDto createVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        validator.validateVacancyDto(vacancyDto);
        return vacancyService.createVacancy(vacancyDto);
    }

    @PutMapping
    @Operation(summary = "Обновить вакансию по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacancy was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VacancyDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vacancy not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public VacancyDto updateVacancy(@Valid @RequestBody VacancyDto vacancyDto) {
        validator.validateVacancyDto(vacancyDto);
        return vacancyService.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/{vacancyId}")
    @Operation(summary = "Удалить вакансию по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacancy deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid vacancy id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vacancy not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public void deleteVacancy(@Parameter(description = "id вакансии которую хотим удалить")
                              @PathVariable Long vacancyId) {
        validator.validateVacancyId(vacancyId);
        vacancyService.deleteVacancy(vacancyId);
    }

    @PostMapping("/filtered")
    @Operation(summary = "Получить все вакансии по фильтрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Vacancies",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VacancyDto.class))}),
            @ApiResponse(responseCode = "404", description = "Vacancy not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public List<VacancyDto> getVacancies(@Valid @RequestBody VacancyFilterDto vacancyFilterDto) {
        return vacancyService.getVacancies(vacancyFilterDto);
    }

    @GetMapping("/{vacancyId}")
    @Operation(summary = "Получить вакансию по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacancy found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = VacancyDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid vacancy id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Vacancy not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public VacancyDto getVacancy(@Parameter(description = "id вакансии которую хотим получить")
                                 @PathVariable Long vacancyId) {
        validator.validateVacancyId(vacancyId);
        return vacancyService.getVacancy(vacancyId);
    }
}
