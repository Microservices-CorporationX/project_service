package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.file_streaming.FileStreamingService;
import faang.school.projectservice.service.project.ProjectFilesService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Tag(name = "Projects methods")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectFilesService projectFilesService;
    private final FileStreamingService fileStreamingService;
    private final ResourceService resourceService;

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

    @Operation(summary = "Upload a file to the project",
            description = "Uploads a file to the specified project's common files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file data or request"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @PostMapping("/{projectId}/resources")
    @Async("filesExecutor")
    public CompletableFuture<ResponseEntity<String>> uploadFile(@PathVariable long projectId,
                                                                @RequestHeader("x-team-member-id") long teamMemberId,
                                                                @RequestBody @NotNull MultipartFile file) {
        projectFilesService.uploadFile(projectId, teamMemberId, file);
        return CompletableFuture.completedFuture(ResponseEntity.ok("File uploaded successfully"));
    }

    @Operation(summary = "Download a file of the project",
            description = "Downloads the specified file from common project's files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @GetMapping("/resources/{resourceId}")
    @Async("filesExecutor")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> downloadFile(@PathVariable long resourceId) {
        InputStream fileStream = projectFilesService.downloadFile(resourceId);
        StreamingResponseBody responseBody = fileStreamingService.getStreamingResponseBody(fileStream);

        return CompletableFuture.completedFuture(ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(getMimeType(resourceId)))
                .body(responseBody));
    }

    @Operation(summary = "Delete a file from the project",
            description = "Deletes the specified file from the project's common files.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File or project not found")
    })
    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<String> deleteFile(@PathVariable long resourceId,
                                             @RequestHeader("x-team-member-id") long teamMemberId) {
        projectFilesService.deleteFile(resourceId, teamMemberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download all project files",
            description = "Downloads all files for the specified project as a zip archive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Project or files not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing")
    })
    @GetMapping("/{projectId}/resources")
    @Async("filesExecutor")
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> downloadAllFiles(@PathVariable long projectId) {
        Map<String, InputStream> files = projectFilesService.downloadAllFiles(projectId);
        StreamingResponseBody responseBody = fileStreamingService.
                getStreamingResponseBodyInZip(files);

        return CompletableFuture.completedFuture(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_" +
                        projectId + "_resources.zip")
                .body(responseBody));
    }

    private String getMimeType(Long resourceId) {
        Resource resource = resourceService.getResource(resourceId);
        return URLConnection.guessContentTypeFromName(resource.getName());
    }
}
