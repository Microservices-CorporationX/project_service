package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
public class StageController {

    public final StageService stageService;

    @PostMapping
    public StageDto createStage(@RequestBody @Valid StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

    @GetMapping("/filters")
    public List<StageDto> getAllStagesByFilters(@RequestBody StageFilterDto stageFilterDto) {
        return stageService.getAllStagesByFilters(stageFilterDto);
    }

    @DeleteMapping("/{stageId}")
    public void deleteStageById(@PathVariable Long stageId) {
        stageService.deleteStageById(stageId);
    }

    @PutMapping("/{stageId}")
    public StageDto updateStage(@PathVariable Long stageId) {
        return stageService.updateStage(stageId);
    }

    @GetMapping("/projects/{projectId}")
    public List<StageDto> getAllStages(@PathVariable Long projectId) {
        return stageService.getAllStagesOfProject(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDto getStageById(@PathVariable Long stageId) {
        return stageService.getStageById(stageId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    //@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public void handleValidationExceptions(MethodArgumentNotValidException ex) {
        throw new IllegalArgumentException("Validation failed", ex);
    }
}
