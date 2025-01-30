package faang.school.projectservice.controller.recommendation;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectInfoDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.ProjectManagementService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/projects")
public class ProjectController {

    private final ProjectManagementService projectManagementService;
    private final UserContext userContext;

    @PostMapping
    public ProjectInfoDto createProject(
            @Valid @RequestBody ProjectCreateDto projectCreateDto) {
        return projectManagementService.createProject(projectCreateDto, userContext.getUserId());
    }

    @PutMapping("/{projectId}")
    public ProjectInfoDto updateProject(
            @Valid @RequestBody ProjectUpdateDto projectUpdateDto,
            @PathVariable long projectId) {
        return projectManagementService.updateProject(projectUpdateDto, projectId, userContext.getUserId());
    }

    @GetMapping
    public List<ProjectInfoDto> getAllProjectsWithFilters(
            @Valid @RequestBody ProjectFilterDto filterDto ) {
        return projectManagementService.getAllProjects(filterDto, userContext.getUserId());
    }

    @GetMapping("/{projectId}")
    public ProjectInfoDto getProjectById(
            @PathVariable long projectId ){
        return projectManagementService.getProjectById(projectId, userContext.getUserId());
    }

}
