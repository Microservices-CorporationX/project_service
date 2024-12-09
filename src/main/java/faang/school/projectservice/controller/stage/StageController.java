package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
@Tag(name = "Контроллер для управления этапами проектов")
public class StageController {

    public final StageService stageService;

    @PostMapping
    @Operation(summary = "Создать этап")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stage created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StageDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Stage already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/filters")
    @Operation(summary = "Получить все этапы по фильтрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Stages",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StageDto.class))}),
            @ApiResponse(responseCode = "404", description = "Stages not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    public List<StageDto> getAllStagesByFilters(@Valid @RequestBody StageFilterDto stageFilterDto) {
        return stageService.getAllStagesByFilters(stageFilterDto);
    }

    @DeleteMapping("/{stageId}")
    @Operation(summary = "Удалить этап по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stage deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid stage id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Stage not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public void deleteStageById(@PathVariable Long stageId) {
        stageService.deleteStageById(stageId);
    }

    @PutMapping("/{stageId}")
    @Operation(summary = "Обновить этап")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stage was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StageDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Stage not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public StageDto updateStage(@PathVariable Long stageId) {
        return stageService.updateStage(stageId);
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Получить все этапы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Stages",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StageDto.class))}),
            @ApiResponse(responseCode = "404", description = "Stages not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public List<StageDto> getAllStages(@PathVariable Long projectId) {
        return stageService.getAllStagesOfProject(projectId);
    }

    @GetMapping("/{stageId}")
    @Operation(summary = "Получить этап по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stage found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StageDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid stage id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Stage not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public StageDto getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }
}
