package faang.school.projectservice.controller;

import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class SubProjectController {
    private final ProjectRepository projectRepository;

    @PostMapping("/{parentId}/subprojects")
    public ResponseEntity<ProjectDto>  createSubProject(
            @PathVariable
            @NotNull(message = "Parent project Id must not be empty")
            @Positive(message = "Parent project Id must be positive integer")
            Long parentId,
            @Valid
            @RequestBody
            CreateProjectDto createProjectDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createSubProject(parentId,createProjectDto));
    }

    @PutMapping("/{parentId}/subprojects")
    public ResponseEntity<ProjectDto> updateSubProject(
            @PathVariable
            @NotNull(message = "Parent project Id must not be empty")
            @Positive(message = "Parent project Id must be positive integer")
            Long parentId,
            @Valid
            @RequestBody
            UpdateSubProjectDto updateSubProjectDto) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateSubProject(parentId, updateSubProjectDto));
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<List<ProjectDto>> filterSubProjects(
            @PathVariable
            @Positive (message = "Parent project Id must be positive integer")
            Long parentId,
            @Valid
            @RequestBody
            SubProjectFiltersDto filters) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.filterSubProjects(parentId, filters));
    }
}
