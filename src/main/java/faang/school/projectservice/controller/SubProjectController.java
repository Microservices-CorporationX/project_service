package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.ProjectValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project/subproject")
public class SubProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;

    @PostMapping("/create")
    public ProjectDto createSubProject(@RequestBody CreateSubProjectDto createSubProjectDto) {
        Long parentId = createSubProjectDto.parentProjectId();
        projectValidator.validateProjectIdNotNull(parentId);

        Project newProject = projectService.createSubProject(parentId,
                subProjectMapper.toSubEntity(createSubProjectDto));
        return projectMapper.toProjectDto(newProject);
    }

    @PutMapping("/update")
    public ProjectDto updateSubProject(@RequestBody UpdateSubProjectDto updateSubProjectDto) {
        Long id = updateSubProjectDto.id();
        projectValidator.validateProjectIdNotNull(id);

        return projectMapper.toProjectDto(
                projectService.updateSubProject(id, updateSubProjectDto.status(), updateSubProjectDto.visibility()));
    }

    @PostMapping("/{id}")
    public List<ProjectDto> getSubProjects(@PathVariable Long projectId,
                                           @RequestParam(defaultValue = "20") Integer limit,
                                           @RequestBody FilterSubProjectDto filtersDto) {
        projectValidator.validateProjectIdNotNull(projectId);

        return projectMapper.toProjectsList(projectService.getSubProjects(projectId, filtersDto, limit));
    }
}
