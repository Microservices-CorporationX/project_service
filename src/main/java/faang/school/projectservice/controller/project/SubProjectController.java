package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    private final UserContext userContext;

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Create sub project in DB")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull Long parentProjectId, @RequestBody @Valid CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentProjectId, subProjectDto);
    }

    @PutMapping("/project/{projectId}")
    @Operation(summary = "Update project by id",
            parameters = @Parameter(name="x-user-id", in = ParameterIn.HEADER, required = true, description = "User id")
    )
    public CreateSubProjectDto updateProject(@PathVariable @Positive @NonNull Long projectId,
                                                @RequestBody @Valid CreateSubProjectDto subProjectDto) {
        long userId = userContext.getUserId();
        return projectService.updateSubProject(projectId, subProjectDto, userId);
    }
}

