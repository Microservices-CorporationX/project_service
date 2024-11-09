package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stages")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @GetMapping("get/all/{projectId}/filter")
    public List<StageDto> getStagesByProjectId(@PathVariable Long projectId,
                                               @RequestParam String role,
                                               @RequestParam String status) {
        return stageService.getStagesByProjectIdRoleAndStatus(projectId, role, status);
    }

    @PostMapping("/create")
    public StageDto createStage(@Valid @RequestBody StageDto stageDto) {
        return stageService.createStage(stageDto);
    }

}
