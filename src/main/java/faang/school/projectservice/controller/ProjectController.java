package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        log.info("Received request to create project with name: {} for owner ID: {}", projectDto.getName(), projectDto.getOwnerId());
        return projectService.createProject(projectDto);
    }

    @PostMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        log.info("Received request to update project ID: {} with new details.", id);
        return projectService.updateProject(id, projectDto);
    }

    @GetMapping
    public List<ProjectDto> findProjects(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) ProjectStatus status,
                                         @RequestParam(required = false) ProjectVisibility visibility) {
        log.info("Received request to find projects with filters - Name: {}, Status: {}, Visibility: {}", name, status, visibility);
        Long userId = getUserIdFromContext();
        return projectService.findProjects(name, status, visibility, userId);
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProjects() {
        log.info("Received request to retrieve all projects");
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {
        log.info("Received request to retrieve project with ID: {}", id);
        return projectService.getProjectById(id);
    }

    private Long getUserIdFromContext() {
        return 1L;
    }
}
