package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Контроллер для управления проектами")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить проект по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid project id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public ProjectDto getProjectById(@Parameter(description = "id проекта который хотим получить")
                                     @PathVariable long id) {
        return projectService.getProjectById(id);
    }

    @GetMapping
    @Operation(summary = "Получить все проекты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Projects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "404", description = "Projects not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects(new ProjectFilterDto());
    }

    @PostMapping("/with-filters")
    @Operation(summary = "Получить все проекты по фильтрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get Projects",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "404", description = "Projects not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    public List<ProjectDto> getAllProjectsByFilters(@RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getAllProjects(projectFilterDto);
    }

    @PostMapping
    @Operation(summary = "Создать проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Project already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    public ProjectDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    @Operation(summary = "Обновить проект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)
    })
    public ProjectDto updateProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

}
