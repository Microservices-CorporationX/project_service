package faang.school.projectservice.controller;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.service.subprojectService.SubProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subprojects")
@AllArgsConstructor
@Slf4j
public class SubprojectController {

    private final SubProjectService projectService;

    @PostMapping("/{parentProjectId}")
    public ResponseEntity<ProjectDto> createSubProject(@PathVariable Long parentProjectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("Received request to create a subproject for the project with ID: {}", parentProjectId);
        if (parentProjectId.equals(createSubProjectDto.getId())) {
            log.warn("Such a subproject already exists: Path ID: {}, Body ID: {}", parentProjectId, createSubProjectDto.getId());
            return ResponseEntity.badRequest().build();
        }
        ProjectDto createdProject = projectService.createSubProject(parentProjectId, createSubProjectDto);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{subProjectId}")
    public ResponseEntity<ProjectDto> updateSubProject(@PathVariable Long subProjectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("Received request to update subproject with ID: {}", subProjectId);

        if (!subProjectId.equals(createSubProjectDto.getId())) {
            log.warn("Subproject ID mismatch: Path ID: {}, Body ID: {}", subProjectId, createSubProjectDto.getId());
            return ResponseEntity.badRequest().build();
        }

        ProjectDto updatedProject = projectService.updateSubProject(subProjectId, createSubProjectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/{projectId}/getSubprojects")
    public ResponseEntity<List<ProjectDto>> getSubProject(@PathVariable Long projectId, @Valid @RequestBody SubprojectFilterDto subprojectFilterDto) {
        log.info("Received request to get subproject with filter: {} for project {}", projectId, subprojectFilterDto); if (projectId == null || subprojectFilterDto == null) {
            return ResponseEntity.badRequest().build();
        }
        List<ProjectDto> subProject = projectService.getSubProject(projectId, subprojectFilterDto);
        if (subProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subProject);
    }
}
