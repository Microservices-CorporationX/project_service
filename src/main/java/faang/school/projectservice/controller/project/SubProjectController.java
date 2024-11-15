package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    @PostMapping("/project")
    public ResponseEntity<ProjectDto> createProject(@RequestBody ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.createProject(projectDto));
    }

    @PostMapping("/project/{parentProjectId}")
    public ResponseEntity<CreateSubProjectDto> createSubProject(@PathVariable @Positive @NotNull Long parentProjectId, @RequestBody CreateSubProjectDto createSubProjectDto) {
        return ResponseEntity.ok(projectService.createSubProject(parentProjectId, createSubProjectDto));
    }

    @PutMapping("project")
    public ResponseEntity<CreateSubProjectDto> update(@RequestBody CreateSubProjectDto dto){
        return ResponseEntity.ok(projectService.update(dto));
    }

    @GetMapping("/project/{projectId}/subprojects")
    public List<CreateSubProjectDto> getProjectsByFilters(@RequestBody ProjectFilterDto filterDto, @PathVariable@Positive @NotNull Long projectId){
        return projectService.getProjectsByFilters(projectId, filterDto);
    }

}
