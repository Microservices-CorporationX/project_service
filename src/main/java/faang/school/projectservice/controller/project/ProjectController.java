package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ChangeTaskStatusDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping
    public ProjectResponseDto createProject(@Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{projectId}")
    public ProjectResponseDto updateProject(@Valid @Positive @PathVariable Long projectId,
                                            @Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectId, projectUpdateDto);
    }

    @PostMapping("/filtered")
    public List<ProjectResponseDto> getAllProjectsWithFilters(@Valid @RequestBody ProjectFilterDto filterDto) {
        return projectService.findAllProjectsWithFilters(filterDto);
    }

    @GetMapping("/all")
    public List<ProjectResponseDto> findAllProjects() {
        return projectService.findAllProject();
    }

    @GetMapping("/get/{projectId}")
    public ProjectResponseDto getProjectById(@Valid @Positive @PathVariable Long projectId) {
        return projectService.getProjectById(projectId);
    }

    @GetMapping("/view/{projectId}")
    public ProjectResponseDto viewProject(@Positive @PathVariable Long projectId) {
        long userId = userContext.getUserId();
        return projectService.viewProject(projectId, userId);
    }

    @PutMapping("/tasks/status")
    @Operation(summary = "Change task status")
    public ChangeTaskStatusDto changeTaskStatus(
            @Valid @RequestBody ChangeTaskStatusDto changeTaskStatusDto) {
        return projectService.changeTaskStatus(changeTaskStatusDto, userContext.getUserId());
    }
}