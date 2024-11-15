package faang.school.projectservice.controller.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.stage.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StageController {

    public final StageService stageService;

    public StageDto createStage(@Valid StageDto stageDto) {
       return stageService.createStage(stageDto);
    }

    public List<StageDto> getAllStagesByFilters(StageFilterDto stageFilterDto) {
       return stageService.getAllStagesByFilters(stageFilterDto);
    }

    public void deleteStageById(Long id) {
        stageService.deleteStageById(id);
    }

    public StageDto updateStage(Long stageId) {
        return stageService.updateStage(stageId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationExceptions(MethodArgumentNotValidException ex) {
        throw new IllegalArgumentException("Validation failed", ex);
    }
}
