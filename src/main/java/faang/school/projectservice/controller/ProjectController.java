package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${project-service.api-version}/project")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return projectService.getProject(projectId);
    }
}
