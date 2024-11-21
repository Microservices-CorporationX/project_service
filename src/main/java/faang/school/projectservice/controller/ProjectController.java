package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "get Project By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Project found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid project id",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Project not found",
                    content = @Content) })
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
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

}
