package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/project-files/{projectId}")
    public ResourceDto addResource(@PathVariable long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, file);
    }

    @GetMapping(value = "/download/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable long resourceId) {
        return ResponseEntity.ok(new InputStreamResource(resourceService.downloadResource(resourceId)));
    }

    @GetMapping("/all-resources-of-project/{projectId}")
    public List<ResourceDto> getProjectResources(@PathVariable long projectId) {
        return resourceService.getProjectResources(projectId);
    }

    @DeleteMapping("/{resourceId}")
    public void deleteResource(@PathVariable long resourceId) {
        resourceService.deleteResource(resourceId);
    }

    @PutMapping("/{resourceId}")
    public void updateResource(@PathVariable long resourceId, @RequestBody MultipartFile file) {
        resourceService.updateResource(resourceId, file);
    }
}
