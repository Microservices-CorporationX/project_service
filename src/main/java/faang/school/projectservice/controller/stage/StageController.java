package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.ActionWithTask;
import faang.school.projectservice.service.stage.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("stages")
public class StageController {

    private final StageService stageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageDto createStage(@RequestBody StageDto stageDto) {
        log.info("Creating a stage: {}", stageDto);
        return stageService.createStage(stageDto);
    }

    @PostMapping("/filter")
    public List<StageDto> getStageWithFilter(@RequestBody StageFilterDto stageFilterDto) {
        log.info("Get all project stages filtered by roles: {}", stageFilterDto);
        return stageService.getStageByFilter(stageFilterDto);
    }

    @DeleteMapping("/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStage(@PathVariable Long stageId,
                            @RequestParam ActionWithTask actionWithTask,
                            @RequestParam(required = false) Long transferStageId) {
        log.info("Delete stage with ID: {}", stageId);
        stageService.deleteStage(stageId, actionWithTask, transferStageId);
    }

    @PutMapping
    public StageDto updateStage(@RequestBody StageDto stageDto) {
        log.info("Update stage: {}", stageDto);
        return stageService.updateStage(stageDto);
    }

    @GetMapping("/projects/{projectId}")
    public List<StageDto> getAllStage(@PathVariable Long projectId) {
        log.info("Get all stages for project: {}", projectId);
        return stageService.getAllProjectStages(projectId);
    }

    @GetMapping("/{id}")
    public StageDto getStageById(@PathVariable Long id) {
        log.info("Get stage by id: {}", id);
        return stageService.getStageById(id);
    }
}