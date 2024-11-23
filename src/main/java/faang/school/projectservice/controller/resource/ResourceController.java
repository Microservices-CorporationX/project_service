package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.RequestDeleteResourceDto;
import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resources")
@Validated
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping(value="/{projectId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ResponseResourceDto>> upload(
            @RequestPart("files") @Size(min = 1) List<MultipartFile> files,
            @PathVariable @Positive long projectId) {
        long userId = userContext.getUserId();
        log.info("Try upload resources received: projectId={}, userId={}, filesCount={}",
                projectId, userId, files.size());

        List<ResponseResourceDto> responseDtos = resourceService.uploadResources(files, projectId, userId);
        log.info("Resources uploaded successfully: projectId={}, userId={}, filesCount={}",
                projectId, userId, files.size());

        return ResponseEntity.ok(responseDtos);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestBody @Valid RequestDeleteResourceDto dto){
        long userId = userContext.getUserId();
        log.info("Try delete resources received: userId={}, fileId={}", userId, dto.getId());
        resourceService.deleteResource(userId, dto);
        log.info("Resources deleted successfully: userId={}, fileId={}", userId, dto.getId());
    }
}
