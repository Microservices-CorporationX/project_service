package faang.school.projectservice.controller;

import faang.school.projectservice.docs.stage.CreateStageDoc;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/stages")
@Tag(name = "Stages", description = "Operations with stages")
public class StageController {
    private final StageService stageService;

    @CreateStageDoc
    @PostMapping("/create")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        log.info("Received request to create stage: {}", stageDto);

        return stageService.createStage(stageDto);
    }
}
