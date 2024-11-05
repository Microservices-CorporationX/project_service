package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/projects")
@Validated
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping("/sub-projects/create")
    public CreateSubProjectDto createSubProject(@RequestBody @Valid ProjectDto projectDto) {
        return subProjectService.createSubProject(projectDto);
    }

    @PatchMapping("/sub-projects/update")
    public ProjectDto updateSubProject(@RequestBody @Valid ProjectDto projectDto) {
        return subProjectService.updateSubProject(projectDto);
    }

    @GetMapping("/sub-projects")
    public List<ProjectDto> getSubProjects(@RequestBody @Valid ProjectDto projectDto) {
        return subProjectService.getSubProjectsByProject(projectDto);
    }

}

