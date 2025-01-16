package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateProjectRequest;
import faang.school.projectservice.dto.project.DeleteProjectRequest;
import faang.school.projectservice.dto.project.FilterProjectRequest;
import faang.school.projectservice.dto.project.ProjectResponse;
import faang.school.projectservice.dto.project.UpdateProjectRequest;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest createProjectRequest) {
        return projectService.createProject(createProjectRequest);
    }

    @PostMapping("/update")
    public ProjectResponse updateProject(@Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        return projectService.updateProject(updateProjectRequest);
    }

    @PostMapping("/{userId}/search")
    public List<ProjectResponse> filterProjects(@Valid @PositiveOrZero @PathVariable Long userId,
                                                @RequestBody FilterProjectRequest filterProjectRequest) {
        return projectService.filterProjects(userId, filterProjectRequest);
    }

    @GetMapping("/{userId}/all")
    public List<ProjectResponse> getProjects(@Valid @PositiveOrZero @PathVariable Long userId) {
        return projectService.getAllProjects(userId);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteProject(@Valid @RequestBody DeleteProjectRequest deleteProjectRequest) {
        projectService.deleteProject(deleteProjectRequest);
        return ResponseEntity.ok().build();
    }

}

