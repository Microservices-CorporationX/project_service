package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ImageType;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
    private final UserContext userContext;
    private static final List<ImageType> imageTypes = List.of(ImageType.png, ImageType.jpg);

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Allows you to check health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping("/{id}/cover")
    public void addCover(@PathVariable Long id, @RequestBody MultipartFile file) {
        checkContentType(file);
        vacancyService.addCover(id, file);
    }

    @GetMapping("/{id}/cover")
    public InputStream getVacancyCover(@PathVariable Long id) {
        return vacancyService.getVacancyCover(id);
    }

    @PutMapping("/{id}/cover")
    public void deleteVacancyCover(@PathVariable Long id) {
        vacancyService.deleteVacancyCover(id, userContext.getUserId());
    }

    private void checkContentType(MultipartFile file) {
        String fileType = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
        if (!imageTypes.contains(fileType)) {
            log.error("Invalid file type.");
            throw new DataValidationException("Invalid file type.");
        }
    }

}
