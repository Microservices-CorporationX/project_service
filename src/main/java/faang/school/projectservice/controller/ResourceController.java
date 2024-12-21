package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.resource.ResourceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceServiceImpl resourceService;
    private final UserContext userContext;

    @GetMapping("/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable Long resourceId) {
        InputStream inputStream = resourceService.downloadResource(resourceId);
        InputStreamResource resourceStream = new InputStreamResource(inputStream);

        HttpHeaders headers = resourceService.getHeaders(resourceId);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resourceStream);
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<String> deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
        return ResponseEntity.ok("Resource deleted successfully");
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable Long resourceId, @RequestBody MultipartFile file) {
        return resourceService.updateResource(resourceId, userContext.getUserId(), file);
    }

    @PostMapping("/{projectId}/add")
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> uploadProjectCover(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {

        String imageUrl = resourceService.uploadProjectCover(projectId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cover image uploaded successfully. URL: " + imageUrl);
    }
}
