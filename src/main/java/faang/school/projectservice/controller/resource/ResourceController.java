package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/resources")
@Slf4j
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping(value = "/{projectId}/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceDto uploadFile(@PathVariable Long projectId,
                                  @PathVariable Long userId,
                                  @RequestParam("file") MultipartFile file){
        log.info("Received a request to upload file: {} ", file.getName());
        return resourceService.uploadFile(projectId, userId, file);
    }

    @GetMapping(value = "/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable Long resourceId){
        log.info("Received a request to get a resource with id: {}", resourceId);

        InputStream resourceStream = resourceService.downloadResource(resourceId);

        return new ResponseEntity<>(new InputStreamResource(resourceStream), HttpStatus.OK);
    }

    @PutMapping(value = "/{resourceId}/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResourceDto updateResource(@PathVariable Long resourceId,
                                      @PathVariable Long userId,
                                      @RequestParam("file") MultipartFile file){
        log.info("Received a request to update a resource with id: {}", resourceId);
        return resourceService.updateResource(userId, resourceId, file);
    }

    @DeleteMapping(value = "/{resourceId}/users/{userId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteResource(@PathVariable Long resourceId,
                               @PathVariable Long userId){
        log.info("Received a request to delete a resource with id: {}", resourceId);
        resourceService.deleteResource(resourceId, userId);
    }
}
