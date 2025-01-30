package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping(value = "/{key}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("key") String key) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.add("Content-Disposition", String.format("attachment; filename=\"%s\"", key));
        InputStream inputStream = resourceService.downloadFile(key);
        return new ResponseEntity<>(new InputStreamResource(inputStream), httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteFile(@PathVariable("key") String key) {
        resourceService.deleteFile(key);
        return ResponseEntity.ok().build();
    }
}
