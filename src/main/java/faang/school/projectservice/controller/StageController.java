package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StageController {

    public final StageService stageService;

    public void createStage(@Valid StageDto stageDto) {
        stageService.createStage(stageDto);
    }
}
