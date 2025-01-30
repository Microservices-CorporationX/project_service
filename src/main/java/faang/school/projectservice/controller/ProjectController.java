package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("{projectId}/presentation")
    public ProjectReadDto generatePdf(@PathVariable long projectId) {
        return projectService.generateProjectPresentation(projectId);
    }

    @GetMapping("{projectId}/presentation")
    public String getPresentationFilePath(@PathVariable long projectId) {
        return projectService.getPresentationFileKey(projectId);
    }
}
