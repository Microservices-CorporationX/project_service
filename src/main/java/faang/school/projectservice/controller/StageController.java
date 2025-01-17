package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage.*;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    public StageResponse createStage(@RequestBody @Valid CreateStageRequest createStageRequest) {
        return stageService.create(createStageRequest);
    }

    public void deleteStage(@RequestBody @Valid DeleteStageRequest deleteStageRequest) {
        stageService.deleteStageWithStrategy(deleteStageRequest);
    }

    public StageResponse updateStage(@RequestBody @Valid UpdateStageRequest updateStageRequest) {
        return stageService.update(updateStageRequest);
    }

    public List<StageResponse> getStagesByProject(@PathVariable Long projectId,
                                                  @RequestParam(required = false) List<String> roles,
                                                  @RequestParam(required = false) String taskStatus) {
        return stageService.getStagesByProjectWithFilters(projectId, roles, taskStatus);
    }

    public List<StageResponse> getAllStagesByProject(@PathVariable Long projectId) {
        return stageService.getAllStagesByProject(projectId);
    }

    public StageResponse getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }
}
