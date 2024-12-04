package faang.school.projectservice.controller;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.SubProjectDto;
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

    @PostMapping("/projects/{parentProjectId}/subprojects")
    public ResponseEntity<SubProjectDto> createSubProject(@PathVariable Long parentProjectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        SubProjectDto createdProject = projectService.createSubProject(parentProjectId, createSubProjectDto);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{subProjectId}")
    public ResponseEntity<SubProjectDto> updateSubProject(@PathVariable Long subProjectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("Received request to update subproject with ID: {}", subProjectId);
            SubProjectDto updatedProject = projectService.updateSubProject(subProjectId, createSubProjectDto);
            return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/{projectId}/getSubprojects")
    public ResponseEntity<List<SubProjectDto>> getSubProject(@PathVariable Long projectId, @Valid @RequestBody SubprojectFilterDto subprojectFilterDto) {
        log.info("Received request to get subproject with filter: {} for project {}", projectId, subprojectFilterDto);
        List<SubProjectDto> subProject = projectService.getSubProject(projectId, subprojectFilterDto);
            return ResponseEntity.ok(subProject);
    }
}
