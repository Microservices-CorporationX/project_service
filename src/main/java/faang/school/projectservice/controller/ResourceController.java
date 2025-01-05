package faang.school.projectservice.controller;

import faang.school.projectservice.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Tag(name = "Resource API", description = "Endpoints for managing project files")
@Slf4j
@Validated
public class ResourceController {
    private final ResourceService resourceService;
    private final Tika tika;

    @PostMapping("/{projectId}")
    @Operation(summary = "Add project cover image.")
    public ResponseEntity<String> uploadProjectCoverImage(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId,
            @RequestParam(name = "file")
            @NotNull(message = "You should add a file") MultipartFile file
    ) {
        log.info("Request to upload cover for project {} by User {}.", projectId, userId);
        resourceService.uploadProjectCover(file, userId, projectId);

        return ResponseEntity.ok(
                String.format("Project %d cover '%s' successfully uploaded by User %d",
                        projectId,
                        file.getOriginalFilename(),
                        userId));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project cover image.")
    public ResponseEntity<String> deleteProjectCoverImage(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId
    ) {
        log.info("Request to delete project {}  cover by User {}.", projectId, userId);
        resourceService.deleteProjectCover(userId, projectId);

        return ResponseEntity.ok(
                String.format("Project %d cover successfully deleted by User %d",
                        projectId,
                        userId));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Download project cover image from storage")
    public ResponseEntity<byte[]> downloadProjectCoverImage(
            @PathVariable
            @Positive(message = "Project id must be a positive integer") Long projectId,
            @RequestParam(name = "userId")
            @Positive(message = "User id must be a positive integer") Long userId
    ) {
        log.info("Request to download project {} cover image by User {}.", projectId, userId);
        byte[] coverImage = resourceService.downloadProjectCover(userId, projectId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, tika.detect(coverImage));

        return ResponseEntity.ok().headers(headers).body(coverImage);
    }
}