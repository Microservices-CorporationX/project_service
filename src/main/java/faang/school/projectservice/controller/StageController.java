package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
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

    @GetMapping("/get/all/{projectId}/filter")
    public List<StageDto> getAllStagesBy(@PathVariable @Positive long projectId,
                                         @NonNull @RequestParam String role,
                                         @NonNull @RequestParam String status) {
        return stageService.getAllStagesBy(projectId, role, status);
    }

    @GetMapping("/get/all/{projectId}")
    public List<StageDto> getAllStagesBy(@PathVariable @Positive long projectId) {
        return stageService.getAllStagesBy(projectId);
    }

    @GetMapping("/get/{stageId}")
    public StageDto getStage(@PathVariable @Positive long stageId) {
        return stageService.getStage(stageId);
    }

    @PostMapping("/create")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @DeleteMapping("/delete/{stageId}")
    public void deleteStage(@PathVariable @Positive long stageId) {
        stageService.deleteStage(stageId);
    }

    @DeleteMapping("/delete/{stageId}/move/tasks/{newStageId}")
    public void deleteStageAndMoveTasks(@PathVariable @Positive long stageId,
                                        @PathVariable @Positive long newStageId) {
        stageService.deleteStageAndMoveTasks(stageId, newStageId);
    }
}
