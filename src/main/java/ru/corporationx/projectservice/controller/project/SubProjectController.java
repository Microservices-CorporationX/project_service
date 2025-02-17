package ru.corporationx.projectservice.controller.project;

import ru.corporationx.projectservice.model.dto.project.CreateSubProjectDto;
import ru.corporationx.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/subProjects")
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    @PostMapping("/{parentProjectId}")
    public ResponseEntity<CreateSubProjectDto> createSubProject(@Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        return ResponseEntity.ok(projectService.createSubProject(createSubProjectDto));
    }

    @PutMapping
    public ResponseEntity<CreateSubProjectDto> updateSubProject(@Valid @RequestBody CreateSubProjectDto dto){
        return ResponseEntity.ok(projectService.updateSubProject(dto));
    }
}
