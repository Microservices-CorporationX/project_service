package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/{parentId}/subprojects")
    public ResponseEntity<ProjectResponseDto>  createSubProject(
            @PathVariable
            @NotNull(message = "Parent project Id must not be empty")
            @Positive(message = "Parent project Id must be positive integer")
            Long parentId,
            @Valid
            @RequestBody
            CreateProjectDto createProjectDto) {
        log.info("Request to create subproject '{}' for parent project id {}", createProjectDto.getName(), parentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createSubProject(parentId,createProjectDto));
    }

    @PutMapping()
    public ResponseEntity<ProjectResponseDto> updateSubProject(@Valid @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        log.info("Request to update subproject id #{}", updateSubProjectDto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateSubProject(updateSubProjectDto));
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<List<ProjectResponseDto>> filterSubProjects(
            @PathVariable
            @Positive (message = "Parent project Id must be positive integer")
            Long parentId,
            @Valid
            @RequestBody
            ProjectFilterDto filters) {
        log.info("Request for all subprojects for parent project id #{}", parentId);
        return ResponseEntity.status(HttpStatus.OK).body(projectService.filterSubProjects(parentId, filters));
    }
}
