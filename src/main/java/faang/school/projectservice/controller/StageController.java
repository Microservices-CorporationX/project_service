package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @GetMapping("{projectId}/filter")
    public List<StageDto> getAllStagesBy(@PathVariable @Positive long projectId,
                                         @RequestBody StageFilterDto stageFilterDto) {
        return stageService.getAllStagesBy(projectId, stageFilterDto);
    }

    @GetMapping("{projectId}")
    public List<StageDto> getAllStagesBy(@PathVariable @Positive long projectId) {
        return stageService.getAllStagesBy(projectId);
    }

    @GetMapping("{stageId}")
    public StageDto getStage(@PathVariable @Positive long stageId) {
        return stageService.getStage(stageId);
    }

    @PostMapping
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @PatchMapping("{stageId}/role")
    public void updateStage(@PathVariable @Positive long stageId,
                            @NonNull @RequestParam String role) {
        stageService.updateStage(stageId, role);
    }


    @DeleteMapping("{stageId}")
    public void deleteStage(@PathVariable @Positive long stageId) {
        stageService.deleteStage(stageId);
    }

    @DeleteMapping("{stageId}/move/tasks/to/{anotherStageId}")
    public void deleteStageAndMoveTasks(@PathVariable @Positive long stageId,
                                        @PathVariable @Positive long anotherStageId) {
        stageService.deleteStageAndMoveTasks(stageId, anotherStageId);
    }
}
