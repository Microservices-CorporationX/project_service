package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
@Tag(name = "Resource API", description = "Endpoints for managing project files")
@Slf4j
@Validated
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/{projectId}")
    @Operation(summary = "Add project file to storage")
    public ResponseEntity<Map<String, Object>> addResource(
            @PathVariable @Positive(message = "Project id must be a positive integer") Long projectId,
            @RequestParam(name = "userId") @Positive(message = "User id must be a positive integer") Long userId,
            @RequestParam(name = "file") @NotEmpty(message = "You should add a file") MultipartFile file
            ) {
        Map<String, Object> response = new HashMap<>();
        ResourceResponseDto resourceResponseDto = resourceService.addResource(projectId, userId, file);
        response.put("data", resourceResponseDto);
        response.put("message", String.format("File %s uploaded successfully", file.getName()));
        log.info("Request to upload of {} file in project id: {} received", file.getName(), projectId);
        return ResponseEntity.ok(response);
    }
}
