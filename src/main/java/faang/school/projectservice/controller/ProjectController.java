package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Validated
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects(new ProjectFilterDto());
    }

    @PostMapping("/with-filters")
    public List<ProjectDto> getAllProjectsByFilters(@RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getAllProjects(projectFilterDto);
    }

    @PostMapping
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

}
