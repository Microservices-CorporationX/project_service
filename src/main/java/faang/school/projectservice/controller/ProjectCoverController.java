package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.FileWriteReadS3Exception;
import faang.school.projectservice.service.ProjectCoverService;
import faang.school.projectservice.utilities.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECT_COVER)
public class ProjectCoverController {

    private final ProjectCoverService projectCoverService;

    @PutMapping(path = UrlUtils.PROJECT_COVER_ID)
    public ResourceDto addResource(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return projectCoverService.add(projectId, file);
    }

    @GetMapping(path = UrlUtils.PROJECT_COVER_ID, produces = "application/octet-stream")
    public ResponseEntity<byte[]> uploadResource(@PathVariable Long projectId) {
        byte[] imageBytes;
        try (InputStream inputStream = projectCoverService.upload(projectId)){
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error get upload data from InputStream.");
            throw new FileWriteReadS3Exception(e.getMessage());
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping(path = UrlUtils.PROJECT_COVER_ID)
    public ResourceDto deleteResource(@PathVariable Long projectId) {
        return projectCoverService.delete(projectId);
    }
}