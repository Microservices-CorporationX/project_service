package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.ActionWithTask;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StageController {

    private final StageService stageService;

    public StageDto createStage(StageDto stageDto){
        log.info("Creating a stage: {}", stageDto);
        return stageService.createStage(stageDto);
    }

    public List<StageDto> getStageWithFilter(StageFilterDto stageFilterDto){
        log.info("Get all project stages filtered by roles: {}", stageFilterDto);
        return stageService.getStageByFilter(stageFilterDto);
    }

    public void deleteStage(Long stageId, ActionWithTask actionWithTask, Long transferStageId){
        log.info("Delete stage with ID: {}", stageId);
        stageService.deleteStage(stageId, actionWithTask, transferStageId);
    }

    public StageDto updateStage(StageDto stageDto){
        log.info("Update stage: {}", stageDto);
        return stageService.updateStage(stageDto);
    }

    public List<StageDto> getAllStage(Long projectId){
        log.info("Get all stages {}", projectId);
        return stageService.getAllProjectStages(projectId);
    }

    public StageDto getStageById(Long id){
        log.info("Get stage by id: {}", id);
        return stageService.getStageById(id);
    }
}
