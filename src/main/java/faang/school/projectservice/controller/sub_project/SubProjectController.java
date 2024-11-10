package faang.school.projectservice.controller.sub_project;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Projects")
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping("/{Id}/createSubProject")
    public ProjectDto createSubProject(@NotNull @Min(0) @RequestParam("Id") Long projectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        return projectService.createSubProject(projectId, createSubProjectDto);
    }

    @PatchMapping("/{Id}/updateSubProject")
    public ProjectDto updateSubProject(@Valid @RequestBody ProjectDto subProjectDto) {
        return projectService.updateSubProject(subProjectDto);
    }

    @GetMapping("/{Id}/getSubProject")
    public List<ProjectDto> getSubProjects(@NotNull @Min(0) @RequestParam("Id") Long projectId, @RequestBody ProjectFilterDto projectFilterDto) {
        return projectService.getSubProjects(projectId, projectFilterDto);
    }
}
