package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sub-projects")
@Validated
public class SubProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping("/project/{parentProjectId}")
    @Operation(summary = "Create sub project in DB")
    public CreateSubProjectDto createSubProject(@PathVariable @Positive @NonNull Long parentProjectId,
                                                @RequestBody CreateSubProjectDto subProjectDto) {
        return projectService.createSubProject(parentProjectId, subProjectDto);
    }

    @PutMapping("/project/{projectId}")
    @Operation(summary = "Update project by id",
            parameters = @Parameter(name = "x-user-id", in = ParameterIn.HEADER, required = true, description = "User id")
    )
    public CreateSubProjectDto updateProject(@PathVariable @Positive @NonNull Long projectId,
                                             @RequestBody CreateSubProjectDto subProjectDto) {
        long userId = userContext.getUserId();
        return projectService.updateProject(projectId, subProjectDto, userId);
    }

    @GetMapping("/project/{projectId}/projects")
    @Operation(summary = "Get sub projects by parent project id and filters")
    public List<CreateSubProjectDto> getProjectsByFilters(@PathVariable @Positive @NonNull Long projectId,
                                                          @RequestBody @NonNull FilterProjectDto filterDto
    ) {
        return projectService.getProjectsByFilter(projectId, filterDto);
    }
}

