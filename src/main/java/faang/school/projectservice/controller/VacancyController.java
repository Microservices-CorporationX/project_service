package faang.school.projectservice.controller;

import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Allows you to check health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("healthy...");
    }

    @PostMapping("/{vacancyId}/add")
    public void addCover(@PathVariable Long vacancyId, @RequestBody MultipartFile file) {
        vacancyService.addCover(vacancyId,file);
//        Ограничение на размер файла — не более 5 МБ.
//        WHere this check?
//        Удаление доступно только автору вакансии или владельцу/менеджеру проекта.
//        Изображение должно быть ужато так, чтобы самая большая сторона не превышала 512 пикселей перед сохранением.
    }

}
