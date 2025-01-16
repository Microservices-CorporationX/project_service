package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.DeleteStageRequest;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.UpdateStageRequest;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    public StageDto giveStage(@RequestBody @Valid StageDto stage) {
        return stageService.create(stage);
    }

    public void deleteStage(@RequestBody @Valid DeleteStageRequest deleteStageRequest) {
        Long stageId = deleteStageRequest.stageId();
        String deletionStrategy = deleteStageRequest.deletionStrategy();
        Long targetStageId = deleteStageRequest.targetStageId();

        stageService.deleteStageWithStrategy(deleteStageRequest);
    }

    public StageDto updateStage(@RequestBody @Valid UpdateStageRequest stage) {
        return stageService.update(stage);
    }

    public List<StageDto> getStagesByProject(Long projectId, List<String> roles, String taskStatus) {
        return stageService.getStagesByProjectWithFilters(projectId, roles, taskStatus);
    }

    public List<StageDto> getAllStagesByProject(Long projectId) {
        return stageService.getAllStagesByProject(projectId);
    }

    public StageDto getStageById(Long stageId) {
        return stageService.getStageById(stageId);
    }
}
