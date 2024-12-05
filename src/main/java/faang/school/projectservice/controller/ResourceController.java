package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.utilities.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RESOURCE)
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @GetMapping(path = UrlUtils.ID, produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@PathVariable("id") Long resourceId) {
        byte[] fileBytes = null;
        try {
            fileBytes = resourceService.downloadResource(resourceId).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping(UrlUtils.ID)
    public long deleteResource(@PathVariable("id") Long resourceId) {
        return resourceService.deleteResource(resourceId, userContext.getUserId());
    }

    @PutMapping(UrlUtils.ID)
    public ResourceDto updateResource(@PathVariable("id") Long resourceId,
                                      @RequestBody MultipartFile file) {
        return resourceService.updateResource(resourceId, userContext.getUserId(), file);
    }

    @PostMapping(path = UrlUtils.ID + UrlUtils.ADD)
    public ResourceDto addResource(@PathVariable("id") Long projectId,
                                   @RequestBody MultipartFile file) {
        return resourceService.addResource(projectId, userContext.getUserId(), file);
    }
}
