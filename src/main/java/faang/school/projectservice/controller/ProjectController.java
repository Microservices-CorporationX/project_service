package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectControllerValidator validator;

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable long projectId) {
        validator.validateId(projectId);
        return projectService.getProjectById(projectId);
    }
}
