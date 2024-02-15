package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @PutMapping
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    @GetMapping
    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    @GetMapping("{projectId}")
    public ProjectDto getById(@PathVariable @Min(1) long projectId) {
        return projectService.getById(projectId);
    }

    @GetMapping("/filters")
    public List<ProjectDto> getByFilters(@ModelAttribute ProjectFilterDto filterDto) {
        return projectService.getAll(filterDto);
    }

    @GetMapping("{projectId}/exist")
    public boolean existProjectById(@PathVariable("projectId") long projectsId) {
        return projectService.existProjectById(projectsId);
    }
}