package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.subprojectdto.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subprojectdto.ProjectReadDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validation.ValidatorSubProjectController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subprojects")
public class SubProjectController {

    private final ProjectService projectService;
    private final ValidatorSubProjectController validatorSubProjectController;


    @PostMapping("/create")
    public ResponseEntity<ProjectReadDto> createSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {

        validatorSubProjectController.validateSubProjectName(createSubProjectDto.getName());
        validatorSubProjectController.validateParentProjectExists(createSubProjectDto.getParentProjectId());
        validatorSubProjectController.validateParentProjectStatus(createSubProjectDto.getParentProjectId(), ProjectStatus.CREATED);

        Project parentProject = projectService.getProjectById(createSubProjectDto.getParentProjectId());
        validatorSubProjectController.validateVisibilityForSubProject(parentProject, createSubProjectDto.getVisibility());

        ProjectReadDto result = projectService.createSubProject(createSubProjectDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectReadDto> updateSubProject(@PathVariable Long id, @RequestBody CreateSubProjectDto createSubProjectDto) {

        validatorSubProjectController.validateSubProjectName(createSubProjectDto.getName());
        validatorSubProjectController.validateParentProjectExists(createSubProjectDto.getParentProjectId());
        validatorSubProjectController.validateParentProjectStatus(createSubProjectDto.getParentProjectId(), ProjectStatus.CREATED);

        Project parentProject = projectService.getProjectById(createSubProjectDto.getParentProjectId());
        validatorSubProjectController.validateVisibilityForSubProject(parentProject, createSubProjectDto.getVisibility());

        ProjectReadDto updatedProject = projectService.updateSubProject(id, createSubProjectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/filter")
    public List<ProjectReadDto> getFilteredSubProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status) {

        return projectService.getFilteredSubProjects(name, status);
    }
}
