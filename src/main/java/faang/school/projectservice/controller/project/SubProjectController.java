package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sub-projects")
@Validated
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Create sub project in DB")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull Long parentProjectId, @RequestBody @Valid CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentProjectId, subProjectDto);
    }
}

