package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PutMapping("/{projectId}/upload/cover")
    public void addCoverToProject(@PathVariable @Min(1) long projectId,
                                  @RequestParam("cover") @NotNull MultipartFile cover) {
        projectService.addCoverToProject(projectId, cover);
    }

    @GetMapping("/{projectId}/cover")
    public InputStream getCover(@PathVariable @Min(1) long projectId) {
        return projectService.getCover(projectId);
    }

    @DeleteMapping("/{projectId}/cover")
    public void deleteAvatar(@PathVariable @Min(1) long projectId) {
        projectService.deleteCover(projectId);
    }
}
