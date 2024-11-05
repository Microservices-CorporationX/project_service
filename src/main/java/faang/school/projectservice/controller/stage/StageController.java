package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.entity.stage.DeleteStrategy;
import faang.school.projectservice.service.stage.StageService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/project/{projectId}")
    public List<StageDto> getProjectStages(
            @Positive @PathVariable Long projectId,
            @Valid StageFilterDto filters) {
        return stageService.getFilteredStages(projectId, filters);
    }

    @DeleteMapping("/{id}")
    public void deleteStage(
            @Positive @PathVariable Long id,
            @RequestParam DeleteStrategy strategy,
            @RequestParam(required = false) Long targetStageId) {
        stageService.deleteStage(id, strategy, targetStageId);
    }

    @PutMapping("/{id}")
    public StageDto updateStage(
            @Positive @PathVariable Long id,
            @Valid @RequestBody StageDto stageDto) {
        return stageService.updateStage(id, stageDto);
    }

    @GetMapping("/{id}")
    public StageDto getStage(@Positive @PathVariable Long id) {
        return stageService.getStage(id);
    }
}