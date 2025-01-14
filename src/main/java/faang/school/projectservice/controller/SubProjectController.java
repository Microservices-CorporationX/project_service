package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/v1/subprojects")
@RestController
public class SubProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createSubProject(@RequestBody CreateSubProjectDto subProjectDto,
                                       @RequestHeader("x-user-id") Long ownerId) {
        validateCreateSubProjectRequest(subProjectDto);
        return projectService.createSubProject(subProjectDto, ownerId);
    }

    @PutMapping("/{id}")
    public ProjectDto updateSubProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        projectDto.setId(id);
        validateUpdateSubProjectRequest(projectDto);
        return projectService.updateSubProject(projectDto);
    }

    @GetMapping("/{parentId}")
    public List<ProjectDto> getSubProjects(@PathVariable Long parentId,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) ProjectStatus status,
                                           @RequestHeader("x-user-id") Long userId) {
        return projectService.getSubProjects(parentId, name, status, userId);
    }

    private void validateCreateSubProjectRequest(CreateSubProjectDto subProjectDto) {
        if (subProjectDto.getName() == null || subProjectDto.getName().isBlank()) {
            throw new IllegalArgumentException("Subproject name cannot be empty");
        }
        if (subProjectDto.getParentProjectId() == null) {
            throw new IllegalArgumentException("Parent project ID is required");
        }
    }

    private void validateUpdateSubProjectRequest(ProjectDto projectDto) {
        if (projectDto.getId() == null) {
            throw new IllegalArgumentException("Project ID is required for update");
        }
        if (projectDto.getStatus() == null) {
            throw new IllegalArgumentException("Project status cannot be null");
        }
    }
}