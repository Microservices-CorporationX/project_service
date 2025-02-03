package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ImageType;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private static final List<ImageType> IMAGE_TYPES = List.of(ImageType.png, ImageType.jpg);

    @PostMapping("/{id}/cover")
    @Operation(summary = "Add cover to vacancy", description = "Allows you to add cover for vacancy")
    public void addCover(@PathVariable Long id, @RequestBody MultipartFile file) {
        checkContentType(file);
        vacancyService.addCover(id, file);
    }

    @GetMapping("/{id}/cover")
    @Operation(summary = "Download cover", description = "Allows you to download cover from vacancy")
    public InputStream getVacancyCover(@PathVariable Long id) {
        return vacancyService.getVacancyCover(id);
    }

    @PutMapping("/{id}/cover")
    @Operation(summary = "Delete vacancy cover", description = "Allows you to delete vacancy cover")
    public void deleteVacancyCover(@PathVariable Long id) {
        vacancyService.deleteVacancyCover(id, userContext.getUserId());
    }

    private void checkContentType(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File is empty.");
        }
        String fileType = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
        if (!IMAGE_TYPES.contains(fileType)) {
            log.error("Invalid file type.");
            throw new DataValidationException("Invalid file type.");
        }
    }

}
