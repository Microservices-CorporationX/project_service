package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceDto> uploadFile(@RequestParam("projectId") long projectId,
                                              @RequestBody MultipartFile file) {
        return new ResponseEntity<>(resourceService.uploadFile(projectId, file), HttpStatus.OK);
    }

    @GetMapping("/{key}")
    public InputStream downloadFile(@PathVariable("key") String key) {
        return resourceService.downloadFile(key);
    }

    @DeleteMapping("/{key}")
    public void deleteFile(@PathVariable("key") String key) {
        resourceService.deleteFile(key);
    }
}
