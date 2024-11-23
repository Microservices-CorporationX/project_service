package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CoverProjectDto;
import faang.school.projectservice.service.project.ProjectCoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectCoverController {
    private final ProjectCoverService projectCoverService;

    @PutMapping("/{projectId}/cover")
    public CoverProjectDto uploadCoverToProject(@PathVariable long projectId, @RequestBody @Validated MultipartFile file) {
        return projectCoverService.addCoverProject(projectId, file);
    }

    @GetMapping("/{projectId}/cover")
    public CoverProjectDto getCoverFromProject(@PathVariable long projectId) {
        return projectCoverService.getCoverProject(projectId);
    }

    @DeleteMapping("/{projectId}/cover")
    public CoverProjectDto deleteCoverFromProject(@PathVariable long projectId) {
        return projectCoverService.deleteCoverProject(projectId);
    }
}
