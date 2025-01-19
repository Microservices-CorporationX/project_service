package faang.school.projectservice.controller;


import faang.school.projectservice.dto.Project.ProjectDto;
import faang.school.projectservice.dto.Project.ProjectFilterDto;
import faang.school.projectservice.dto.Project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        log.info("Creating project '{}' by UserId #{}.", projectDto.getName(), projectDto.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDto));
    }

    @PutMapping
    public ResponseEntity<ProjectDto> updateProjectDescription(@Valid @RequestBody ProjectUpdateDto dto) {
        log.info("Updating project {} by userId {} .", dto.getName(), dto.getOwnerId());
        return ResponseEntity.ok(projectService.updateProject(dto));
    }

    @GetMapping("/filteredName/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilterName(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting filtered projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getProjectsByFilterName(filterDto, currentUserId));
    }

    @GetMapping("/filteredStatus/{currentUserId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByFilterStatus(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting filtered projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getProjectsByFilterStatus(filterDto, currentUserId));
    }

    @GetMapping("/allProjects/{currentUserId}")
    public ResponseEntity<List<Project>> getAllUserAvailableProjects(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @NotNull(message = "CurrentUserId is required.")
            @Positive(message = "CurrentUserId must be greater than 0.")
            @PathVariable
            Long currentUserId) {
        log.info("Getting available projects by User #{}.", currentUserId);
        return ResponseEntity.ok(projectService.getAllUserAvailableProjects(currentUserId));
    }

    @GetMapping("/filteredName/{currentUserId}")
    public ResponseEntity<Project> findProjectById(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @NotNull(message = "Id is required.")
            @Positive(message = "Id must be greater than 0.")
            @PathVariable
            Long id) {
        log.info("Getting project with #{}.", id);
        return ResponseEntity.ok(projectService.findProjectById(id));
    }
}