package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        Long userId = userContext.getUserId();
        ProjectDto createdProject = projectService.createProject(projectDto, userId);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long projectId,
                                                    @RequestBody ProjectDto projectDto) {
        projectDto.setId(projectId);
        ProjectDto updatedProject = projectService.updateProject(projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status) {
        Long userId = userContext.getUserId();
        List<ProjectDto> projects = projectService.getProjects(name, status, userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        ProjectDto project = projectService.getProjectById(projectId, userId);
        return ResponseEntity.ok(project);
    }
}
