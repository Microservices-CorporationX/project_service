package faang.school.projectservice.controller;

import faang.school.projectservice.docs.project.AllProjectsDoc;
import faang.school.projectservice.docs.project.CreateProjectDoc;
import faang.school.projectservice.docs.project.FiltersProjectDoc;
import faang.school.projectservice.docs.project.ProjectsDoc;
import faang.school.projectservice.docs.project.UpdateProjectDoc;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
@Tag(name = "Project", description = "The project operations")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @CreateProjectDoc
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto dto) {
        log.info("Creating project '{}' by UserId {}.", dto.getName(), dto.getOwnerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(dto));
    }

    @PutMapping
    @UpdateProjectDoc
    public ResponseEntity<UpdateProjectDto> updateProject(@Valid @RequestBody UpdateProjectDto dto) {
        log.info("Updating project '{}' by UserId {}.", dto.getName(), dto.getOwnerId());

        return ResponseEntity.ok(projectService.updateProject(dto));
    }

    @FiltersProjectDoc
    @GetMapping("/filters/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilter(
            @NotBlank(message = "Project name must not be empty.")
            @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters.")
            String name,
            ProjectStatus status,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting filtered projects by User {}.", currentUserId);

        ProjectFilterDto filterDto = new ProjectFilterDto(name, status);

        return ResponseEntity.ok(projectService.getProjectsByFilter(filterDto, currentUserId));
    }

    @AllProjectsDoc
    @GetMapping("/all-projects")
    public ResponseEntity<List<ProjectDto>> getAllProjects(
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @RequestParam
            Long currentUserId) {
        log.info("Getting all projects by User {}.", currentUserId);

        return ResponseEntity.ok(projectService.getAllProjectsForUser(currentUserId));
    }

    @ProjectsDoc
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(
            @Positive(message = "CurrentUserId must be greater than 0.")
            @RequestParam("currentUserId")
            Long currentUserId,
            @Positive(message = "ProjectId must be greater than 0.")
            @PathVariable
            Long projectId) {
        log.info("Getting project id #{} by User {}.", projectId, currentUserId);

        return ResponseEntity.ok(projectService.getAccessibleProjectById(currentUserId, projectId));
    }
}