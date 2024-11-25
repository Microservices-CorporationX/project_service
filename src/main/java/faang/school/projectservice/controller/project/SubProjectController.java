package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<CreateSubProjectDto> update(@Valid @RequestBody CreateSubProjectDto dto){
        return ResponseEntity.ok(projectService.update(dto));
    }
}
