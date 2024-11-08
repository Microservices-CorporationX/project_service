package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.DeletionType;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
@Validated
@Slf4j
public class StageController {

    private final StageService stageService;

    @PostMapping()
    public StageDto createStage(@Validated(StageDto.Before.class) @RequestBody StageDto stageDto) {
        log.info("Received request to create a stage for the project with ID: {}", stageDto.getProjectId());
        return stageService.createStage(stageDto);
    }

    @GetMapping("/projects/{projectId}")
    public List<StageDto> getFilteredProjectStages(@PathVariable("projectId") Long projectId,
                                                     @RequestParam(required = false) TeamRole teamRole,
                                                     @RequestParam(required = false) TaskStatus taskStatus) {
        log.info("Received request to get stages filtered by team role {} and task status {} for the project with ID: {}",
                teamRole, taskStatus, projectId);
        return stageService.getFilteredProjectStages(projectId, teamRole, taskStatus);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable("stageId") Long stageId,
                            @RequestParam DeletionType deletionType,
                            @RequestParam(required = false) Long targetStageId) {
        log.info("Received request to delete the stage with ID: {}, deletion type: {}, transfer stage tasks to target stage ID: {}",
                stageId, deletionType, targetStageId);
        stageService.delete(stageId, deletionType, targetStageId);
    }

    @PatchMapping("/{stageId}")
    public StageDto updateStage(@PathVariable("stageId") Long stageId,
                                @RequestBody StageDto stageDto) {
        log.info("Received request to update the stage with ID: {}", stageId);
        return stageService.update(stageId, stageDto);
    }

    @GetMapping("/projects/{projectId}/all")
    public List<StageDto> getAllStagesByProjectId(@PathVariable("projectId") Long projectId) {
        log.info("Received request to get all stages of the project with ID: {}", projectId);
        return stageService.getAllStagesByProjectId(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable("stageId") Long stageId) {
        log.info("Received request to get the stage with ID: {}", stageId);
        return stageService.getStageById(stageId);
    }
}
