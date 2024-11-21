package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceRequestDto;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> upload(
            @RequestPart("files") @Size(min = 1) List<MultipartFile> files,
            @RequestPart @Valid ResourceRequestDto requestDto) {
        long userId = userContext.getUserId();
        log.info("Try upload resources received: projectId={}, userId={}, filesCount={}",
                requestDto.getProjectId(), userId, files.size());

        List<String> fileKeys = resourceService.upload(files, requestDto, userId);
        log.info("Resources uploaded successfully: projectId={}, userId={}, filesCount={}",
                requestDto.getProjectId(), userId, files.size());

        return ResponseEntity.ok(fileKeys);
    }
}
