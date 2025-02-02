package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
    private final UserContext userContext;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Allows you to check health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping("/{id}")
    public void addCover(@PathVariable Long id, @RequestBody MultipartFile file) {
        vacancyService.addCover(id, file);
    }

    @GetMapping("/{id}")
    public InputStream getVacancyCover(@PathVariable Long id) {
        return vacancyService.getVacancyCover(id);
    }

    @DeleteMapping("/{id}")
    public void deleteVacancyCover(@PathVariable Long id) {
        vacancyService.deleteVacancyCover(id, userContext.getUserId());
    }
}
