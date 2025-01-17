package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.config.context.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<ProjectResponseDto> createProject(
            @Valid @RequestBody CreateProjectRequestDto projectRequestDto) {
        Long userId = userContext.getUserId();
        ProjectResponseDto createdProject = projectService.createProject(projectRequestDto, userId);
        return ResponseEntity.ok(createdProject);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectRequestDto projectRequestDto) {
        ProjectResponseDto updatedProject = projectService.updateProject(projectId, projectRequestDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status) {
        Long userId = userContext.getUserId();
        List<ProjectResponseDto> projects = projectService.getProjects(name, status, userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long projectId) {
        Long userId = userContext.getUserId();
        ProjectResponseDto project = projectService.getProjectById(projectId, userId);
        return ResponseEntity.ok(project);
    }
}