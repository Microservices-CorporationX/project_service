package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.subprojectdto.SubProjectCreateDto;
import faang.school.projectservice.dto.client.subprojectdto.ProjectReadDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validation.ValidatorSubProjectController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subprojects")
public class SubProjectController {

    private final ProjectService projectService;
    private final ValidatorSubProjectController validatorSubProjectController;

    @PostMapping
    public ProjectReadDto createSubProject(@RequestBody SubProjectCreateDto subProjectCreateDto) {

        validatorSubProjectController.validateSubProjectName(subProjectCreateDto.getName());
        validatorSubProjectController.validateParentProjectExists(subProjectCreateDto.getParentProjectId());
        validatorSubProjectController.validateParentProjectStatus(subProjectCreateDto.getParentProjectId(), ProjectStatus.CREATED);

        Project parentProject = projectService.getProjectById(subProjectCreateDto.getParentProjectId());
        validatorSubProjectController.validateVisibilityForSubProject(parentProject, subProjectCreateDto.getVisibility());

        ProjectReadDto result = projectService.createSubProject(subProjectCreateDto);

        return projectService.createSubProject(subProjectCreateDto);
    }

    @PutMapping
    public ProjectReadDto updateSubProject(@PathVariable Long id, @RequestBody SubProjectCreateDto subProjectCreateDto) {

        validatorSubProjectController.validateSubProjectName(subProjectCreateDto.getName());
        validatorSubProjectController.validateParentProjectExists(subProjectCreateDto.getParentProjectId());
        validatorSubProjectController.validateParentProjectStatus(subProjectCreateDto.getParentProjectId(), ProjectStatus.CREATED);

        Project parentProject = projectService.getProjectById(subProjectCreateDto.getParentProjectId());
        validatorSubProjectController.validateVisibilityForSubProject(parentProject, subProjectCreateDto.getVisibility());

        ProjectReadDto updatedProject = projectService.updateSubProject(id, subProjectCreateDto);
        return projectService.updateSubProject(id, subProjectCreateDto);
    }

    @GetMapping
    public List<ProjectReadDto> getFilteredSubProjects(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status) {

        return projectService.getFilteredSubProjects(name, status);
    }
}
