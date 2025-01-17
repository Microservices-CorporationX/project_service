package faang.school.projectservice.controller.recommendation;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    public ProjectDto createProject(
            @Valid @RequestBody ProjectDto projectDto,
            @RequestParam @NotNull long userId) {
        return projectService.createProject(projectDto, userId);
    }

    public ProjectDto updateProject(
            @Valid @RequestBody ProjectDto projectDto,
            @RequestParam @NotNull long userId) {
        return projectService.updateProject(projectDto, userId);
    }

    public List<ProjectDto> getAllProjectsWithFilters(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @RequestParam @NotNull long userId) {
        return projectService.getAllProjectsWithFilters(filterDto, userId);
    }

    public List<ProjectDto> getAllProjects(@RequestParam @NotNull long userId){
        return projectService.getAllProjects(userId);
    }

    public ProjectDto getProjectById(
            @RequestParam @NotNull long projectId,
            @RequestParam @NotNull long userId){
        return projectService.getProjectById(projectId, userId);
    }

}
