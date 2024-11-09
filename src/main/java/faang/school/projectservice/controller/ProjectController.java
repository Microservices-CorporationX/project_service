package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto dto) {
        log.info("Creating project '{}' by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(dto));
    }

    @PutMapping
    public ResponseEntity<UpdateProjectDto> updateProjectDescription(@Valid @RequestBody UpdateProjectDto dto) {
        log.info("Updating project '{}' by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.ok(projectService.updateProject(dto));
    }

    @GetMapping("/filtered/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilter(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting filtered projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getProjectsByFilter(filterDto, currentUserId));
    }

    @GetMapping("/all-projects/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getAllProjects(
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting all projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getAllProjectsToUser(currentUserId));
    }

    @GetMapping("/project/{currentUserId}")
    public ResponseEntity<ProjectDto> getProjectById(
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            Long projectId) {
        log.info("Getting project id #{} by User #{}.", projectId, currentUserId);
        return ResponseEntity.ok(projectService.getUserAvailableProjectById(currentUserId, projectId));
    }
}