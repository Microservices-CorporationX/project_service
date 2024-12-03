package faang.school.projectservice.controller;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/{parentProjectId}/subprojects")
    public ProjectDto createSubProject(@Positive @PathVariable Long parentProjectId,
                                       @RequestBody @Valid CreateSubProjectDto createDto) {
        return projectService.createSubProject(parentProjectId, createDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateSubProject(@Positive @PathVariable Long projectId,
                                       @RequestBody UpdateSubProjectDto updateDto) {
        return projectService.updateSubProject(projectId, updateDto);
    }

    @GetMapping("/{parentProjectId}/subprojects")
    public List<ProjectDto> getFilteredSubProjects(@Positive @PathVariable Long parentProjectId,
                                                   @RequestBody SubProjectFilterDto filterDto) {
        return projectService.getFilteredSubProjects(parentProjectId, filterDto);
    }
}
