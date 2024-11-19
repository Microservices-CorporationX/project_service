package faang.school.projectservice.controller;

import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
        return stageService.getStagesByProjectIdFiltered(projectId, stageFilterDto);
    }

    @GetMapping("{projectId}")
    public List<StageDto> getAllStagesBy(@PathVariable @Positive long projectId) {
        return stageService.getStagesByProjectId(projectId);
    }

    @GetMapping("{stageId}")
    public StageDto getStage(@PathVariable @Positive long stageId) {
        return stageService.getStageDtoById(stageId);
    }

    @PostMapping
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @PutMapping("{stageId}/executor")
    public void updateStage(@PathVariable @Positive long stageId,
                            @Valid @RequestBody TeamMemberDto teamMemberDto) {
        stageService.updateStage(stageId, teamMemberDto);
    }

    @DeleteMapping("{stageId}")
    public void deleteStage(@PathVariable @Positive long stageId) {
        stageService.deleteStage(stageId);
    }

    @DeleteMapping("{stageId}/move/tasks/to/{anotherStageId}")
    public void deleteStageAndMoveTasks(@PathVariable @Positive long stageId,
                                        @PathVariable @Positive long anotherStageId) {
        stageService.deleteStage(stageId, anotherStageId);
    }
}
