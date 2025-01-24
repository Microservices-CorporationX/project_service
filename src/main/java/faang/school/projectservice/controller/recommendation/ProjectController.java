package faang.school.projectservice.controller.recommendation;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectReadDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectReadDto createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @PutMapping("/{userId}")
    public ProjectReadDto updateProject(
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto,
            @PathVariable @NotNull long userId) {
        return projectService.updateProject(projectUpdateDto, userId);
    }

    @PostMapping("/filter")
    public List<ProjectReadDto> getAllProjectsWithFilters(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @RequestParam @NotNull long userId) {
        return projectService.getAllProjectsWithFilters(filterDto, userId);
    }

    @GetMapping
    public List<ProjectReadDto> getAllProjects(@RequestParam(required = true) long userId){
        return projectService.getAllProjects(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectReadDto getProjectById(
            @PathVariable(required = true) long projectId,
            @RequestParam(required = true) long userId){
        return projectService.getProjectById(projectId, userId);
    }

}
