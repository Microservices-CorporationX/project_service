package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto dto) {
        log.info("Creating project '{}' by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(dto));
    }

    @PutMapping("/description-update")
    public ResponseEntity<UpdateProjectDto> updateProjectDescription(@Valid @RequestBody UpdateProjectDto dto) {
        log.info("Updating project '{}' description by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.ok(projectService.updateProjectDescription(dto));
    }

    @PutMapping("/status-update")
    public ResponseEntity<UpdateProjectDto> updateProjectStatus(@Valid @RequestBody UpdateProjectDto dto) {
        log.info("Updating project '{}' status by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.ok(projectService.updateProjectStatus(dto));
    }

    @PutMapping("/visibility-update")
    public ResponseEntity<UpdateProjectDto> updateProjectVisibility(@Valid @RequestBody UpdateProjectDto dto) {
        log.info("Updating project '{}' visibility by UserId #{}.", dto.getName(), dto.getOwnerId());
        return ResponseEntity.ok(projectService.updateProjectVisibility(dto));
    }
}