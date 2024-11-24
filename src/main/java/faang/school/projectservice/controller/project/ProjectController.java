package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.service.project.ProjectFilesService;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable long resourceId) {
        InputStream fileStream = projectFilesService.downloadFile(resourceId);
        return getStreamingResponseBodyResponseEntity(fileStream);
    }

    @GetMapping("/{projectId}/resources")
    public ResponseEntity<StreamingResponseBody> downloadAllFiles(@PathVariable long projectId) {
        Map<String, InputStream> files = projectFilesService.downloadAllFiles(projectId);

        StreamingResponseBody responseBody = outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                for (Map.Entry<String, InputStream> entry : files.entrySet()) {
                    String fileName = entry.getKey();
                    InputStream fileStream = entry.getValue();

                    try (fileStream) {
                        zipOut.putNextEntry(new ZipEntry(fileName));

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, bytesRead);
                        }
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        // Log the error and continue with other files
                        System.err.println("Error processing file: " + fileName + ". Skipping...");
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while zipping files", e);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project_" +
                        projectId + "_resources.zip")
                .body(responseBody);
    }

    @DeleteMapping("/resources/{resourceId}/{teamMemberId}")
    public ResponseEntity<String> deleteFile(@PathVariable long resourceId, @PathVariable long teamMemberId) {
        projectFilesService.deleteFile(resourceId, teamMemberId);
        return ResponseEntity.ok("File deleted successfully");
    }

    @NotNull
    private ResponseEntity<StreamingResponseBody> getStreamingResponseBodyResponseEntity(InputStream fileStream) {
        StreamingResponseBody responseBody = outputStream -> {
            try (fileStream) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new FileDownloadException("Error streaming file" + Arrays.toString(e.getStackTrace()));
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(responseBody);
    }
}
