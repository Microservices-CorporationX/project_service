package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.create(projectDto));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<ProjectDto> updateStatus(@PathVariable @NonNull Long id, @RequestBody @NonNull ProjectStatus projectStatus) {
        return ResponseEntity.ok(projectService.updateStatus(projectStatus, id));
    }

    @PatchMapping("/update-description/{id}")
    public ResponseEntity<ProjectDto> updateDescription(@PathVariable @NonNull Long id, @RequestBody @NonNull String description) {
        return ResponseEntity.ok(projectService.updateDescription(description, id));
    }

    @GetMapping("/filtered")
    public List<ProjectDto> findWithFilters(@RequestBody @NonNull ProjectFilterDto filters) {
        return projectService.findWithFilters(filters);
    }

    @GetMapping
    public List<ProjectDto> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> findById(@PathVariable @NonNull Long id) {
        return projectService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private void validateRecommendation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        throw new DataValidationException(errorMessage);
    }
}
