package faang.school.projectservice.controller.sub_project;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Projects")
@Tag(name = "Контроллер для управления подпроектами")
public class SubProjectController {

    private final ProjectService projectService;

    @PostMapping("/{Id}/createSubProject")
    @Operation(summary = "Создать подпроект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SubProject created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "SubProject already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public ProjectDto createSubProject(@PathVariable("Id") Long projectId,
                                       @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        return projectService.createSubProject(projectId, createSubProjectDto);
    }

    @PatchMapping("/{Id}/updateSubProject")
    @Operation(summary = "Обновить подпроект")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubProject was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Non-validate parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "SubProject not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public ProjectDto updateSubProject(@Valid @RequestBody ProjectDto subProjectDto) {
        return projectService.updateSubProject(subProjectDto);
    }

    @PostMapping("/{Id}/getSubProject")
    @Operation(summary = "Получить подпроект по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SubProject found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProjectDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid subProject id",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "SubProject not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    public List<ProjectDto> getSubProjects(@PathVariable("Id") Long projectId,
                                           @Valid @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getSubProjects(projectId, projectFilterDto);
    }
}
