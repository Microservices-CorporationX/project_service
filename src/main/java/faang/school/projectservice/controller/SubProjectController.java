package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/projects")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/subprojects")
    public ProjectDto create(@Valid @RequestBody CreateSubProjectDto createDto) {
        return projectService.create(createDto);
    }

    @PutMapping("/subprojects")
    public ProjectDto update(@PathVariable long subProjectId, @Valid @RequestBody UpdateSubProjectDto updateDto) {
        return projectService.update(updateDto);
    }

    @GetMapping("/{projectId}")
    public List<ProjectDto> getSubProjects(@PathVariable long projectId) {
        return projectService.getSubProjects(projectId);
    }

}
