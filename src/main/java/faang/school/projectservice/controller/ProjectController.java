package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.create(projectDto));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<ProjectDto> updateStatus(@PathVariable Long id, @RequestBody ProjectStatus projectStatus) {
        return ResponseEntity.ok(projectService.updateStatus(projectStatus, id));
    }

    @PatchMapping("/update-description/{id}")
    public ResponseEntity<ProjectDto> updateDescription(@PathVariable Long id, @RequestBody String description) {
        return ResponseEntity.ok(projectService.updateDescription(description, id));
    }

    @GetMapping("/filtered")
    public List<ProjectDto> findWithFilters(@RequestBody ProjectFilterDto filters) {
        return projectService.findWithFilters(filters);
    }

    @GetMapping
    public List<ProjectDto> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> findById(@PathVariable Long id) {
        return projectService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
