package faang.school.projectservice.controller.recommendation;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectManagementService projectManagementService;

    @PostMapping
    public ProjectInfoDto createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectManagementService.createProject(projectCreateDto);
    }

    @PutMapping("/{userId}")
    public ProjectInfoDto updateProject(
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto,
            @PathVariable @NotNull long userId) {
        return projectManagementService.updateProject(projectUpdateDto, userId);
    }

    @PostMapping("/filter")
    public List<ProjectInfoDto> getAllProjectsWithFilters(
            @Valid @RequestBody ProjectFilterDto filterDto,
            @RequestParam @NotNull long userId) {
        return projectManagementService.getAllProjectsWithFilters(filterDto, userId);
    }

    @GetMapping
    public List<ProjectInfoDto> getAllProjects(@RequestParam(required = true) long userId){
        return projectManagementService.getAllProjects(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectInfoDto getProjectById(
            @PathVariable(required = true) long projectId,
            @RequestParam(required = true) long userId){
        return projectManagementService.getProjectById(projectId, userId);
    }

}
