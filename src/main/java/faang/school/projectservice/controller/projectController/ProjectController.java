package faang.school.projectservice.controller.projectController;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto createProject(@RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{projectId}")
    public ProjectResponseDto updateProject(@PathVariable @Min(1) Long projectId,
                                            @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectId, projectUpdateDto);
    }

    @PostMapping("/filtered")
    public List<ProjectResponseDto> getAllProjectsWithFilters(@RequestBody ProjectFilterDto filterDto) {
        return projectService.findAllProjectsWithFilters(filterDto);
    }

    @GetMapping("/all")
    public List<ProjectResponseDto> findAllProjects() {
        return projectService.findAllProject();
    }

    @GetMapping("/get/{projectId}")
    public ProjectResponseDto getProjectById(@PathVariable @Min(1) Long projectId) {
        return projectService.getProjectById(projectId);
    }
}
