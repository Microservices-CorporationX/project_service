package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    public ProjectDto create(@Valid @RequestBody CreateSubProjectDto createDto) {
        return projectService.create(createDto);
    }

    public ProjectDto update(@Valid @RequestBody UpdateSubProjectDto updateDto) {
        return projectService.update(updateDto);
    }

    public List<ProjectDto> getSubProjects(long projectId) {
        return projectService.getSubProjects(projectId);
    }

}
