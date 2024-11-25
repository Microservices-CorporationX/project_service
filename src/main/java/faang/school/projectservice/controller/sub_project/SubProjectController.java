package faang.school.projectservice.controller.sub_project;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Projects")
@Validated
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/{Id}/createSubProject")
    public ProjectDto createSubProject(@NotNull @Min(0) @PathVariable("Id") Long projectId,
                                       @RequestBody CreateSubProjectDto createSubProjectDto) {
        return projectService.createSubProject(projectId, createSubProjectDto);
    }

    @PatchMapping("/{Id}/updateSubProject")
    public ProjectDto updateSubProject(@RequestBody ProjectDto subProjectDto) {
        return projectService.updateSubProject(subProjectDto);
    }

    @PostMapping("/{Id}/getSubProject")
    public List<ProjectDto> getSubProjects(@NotNull @Min(0) @PathVariable("Id") Long projectId,
                                           @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getSubProjects(projectId, projectFilterDto);
    }
}
