package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto,
                                    @RequestHeader("x-user-id") @Positive Long userId) {
        return projectService.createProject(projectDto, userId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto findProject(@PathVariable @Positive long projectId) {
        return projectService.findById(projectId);
    }

    @PostMapping("/filter")
    public List<ProjectDto> findAllProjects(@RequestBody @Valid ProjectFilterDto filters,
                                            @RequestHeader("x-user-id") @Positive Long userId) {
        return projectService.findAllProjects(filters, userId);
    }

    @PutMapping
    public ProjectDto updateProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }
}
