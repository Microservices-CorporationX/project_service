package faang.school.projectservice.controller.project_file;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectFileController {
    private final ResourceService resourceService;

    @PostMapping("/{projectId}")
    public ResourceDto addResource(@PathVariable long projectId, @RequestBody MultipartFile multipartFile) {
        return resourceService.addResource(projectId, multipartFile);
    }

    @PutMapping("/{resourceId}")
    public ResourceDto updateResource(@PathVariable long resourceId, @RequestBody MultipartFile multipartFile) {
        return resourceService.updateResource(resourceId, multipartFile);
    }

    @DeleteMapping("/{resourceId}")
    public ResourceDto removeResource(@PathVariable long resourceId) {
        return resourceService.removeResource(resourceId);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<String> getFileUrl(@PathVariable long resourceId) {
        String presignedUrl = resourceService.generatePresignedUrl(resourceId);

        return ResponseEntity.ok(presignedUrl);
    }

}
