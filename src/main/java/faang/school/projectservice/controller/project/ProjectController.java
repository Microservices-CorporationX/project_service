package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectFilesService;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Projects methods")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectFilesService projectFilesService;

    @Operation(summary = "Create a new project",
            description = "Creates a new project with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })

    @PostMapping
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto,
                                    @Parameter(description = "User ID of the requester." +
                                            " Must be a positive number.")
                                    @RequestHeader("x-user-id") @Positive Long userId) {
        return projectService.createProject(projectDto, userId);
    }

    @Operation(summary = "Get project details",
            description = "Fetches the project details based on the project ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })

    @GetMapping("/{projectId}")
    public ProjectDto findProject(@PathVariable @Positive long projectId) {
        return projectService.findById(projectId);
    }

    @Operation(summary = "Get list of projects",
            description = "Fetches a list of projects based on the applied filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter data")
    })

    @PostMapping("/filter")
    public List<ProjectDto> findAllProjects(@RequestBody @Valid ProjectFilterDto filters,
                                            @Parameter(description = "User ID of the requester. " +
                                                    "Must be a positive number.")
                                            @RequestHeader("x-user-id") @Positive Long userId) {
        return projectService.findAllProjects(filters, userId);
    }

    @Operation(summary = "Update project details",
            description = "Updates an existing project's details with the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })

    @PutMapping
    public ProjectDto updateProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }

    //do teamMemberId through Header
    @PutMapping("/{projectId}/resources/{teamMemberId}")
    public ResponseEntity<String> uploadFile(@PathVariable long projectId, @PathVariable long teamMemberId,
                           @RequestBody MultipartFile file) {
        projectFilesService.uploadFile(projectId, teamMemberId, file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping("/resources/{resourceId}")
    public byte[] downloadFile(@PathVariable long resourceId) {
        return projectFilesService.downloadFile(resourceId);
    }

    @DeleteMapping("/resources/{resourceId}/{teamMemberId}")
    public ResponseEntity<String> deleteFile(@PathVariable long resourceId, @PathVariable long teamMemberId) {
        projectFilesService.deleteFile(resourceId,teamMemberId);
        return ResponseEntity.ok("File deleted successfully");
    }

    //is this method necessary ???
//    @GetMapping("/{projectId}/resources")
//    public byte[] getAllFiles(@PathVariable long projectId) {
//        return projectFilesService.getAllFiles(projectId);
//    }
}
