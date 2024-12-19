package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.service.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;
    private final ResourceRepository resourceRepository;

    @GetMapping("/{projectId}/all")
    public List<ResourceDto> getAllAvailableResources(@PathVariable Long projectId) {
        return resourceService.getAvailableResources(projectId, userContext.getUserId());
    }

//    @GetMapping(path = "/{resourceId}", produces = "application/octet-stream")
//    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {
//        byte[] imageBytes = null;
//        try {
//            imageBytes = resourceService.downloadResource(resourceId).readAllBytes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_JPEG);
//        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable Long resourceId) {
        try {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Resource with %s id not found", resourceId)));
            InputStream inputStream = resourceService.downloadResource(resourceId);
            InputStreamResource resourceStream = new InputStreamResource(inputStream);

            MediaType mediaType = getMediaTypeForResourceType(resource.getType());

            long contentLength = resource.getSize().longValue();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(contentLength);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(resource.getName())
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceStream);
        } catch (EntityNotFoundException e) {
            log.error("Resource not found with id {}: {}", resourceId, e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) { // Ловим все остальные исключения
            log.error("Error downloading resource with id {}: {}", resourceId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    private MediaType getMediaTypeForResourceType(ResourceType resourceType) {
        return switch (resourceType) {
            case PDF -> MediaType.APPLICATION_PDF;
            case IMAGE -> MediaType.IMAGE_JPEG;
            case VIDEO -> MediaType.valueOf("video/mp4");
            case AUDIO -> MediaType.valueOf("audio/mpeg");
            case MSWORD ->
                    MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case MSEXCEL ->
                    MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case TEXT -> MediaType.TEXT_PLAIN;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
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
}
