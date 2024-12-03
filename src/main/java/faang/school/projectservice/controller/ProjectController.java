package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCreateReq;
import faang.school.projectservice.dto.project.ProjectPatchReq;
import faang.school.projectservice.dto.project.ProjectResp;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.utilities.UrlUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECTS)
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public void createProject(@Valid @RequestBody ProjectCreateReq projectCreateReq) {
        projectService.createProject(projectCreateReq);
    }

    @PatchMapping
    public void patchProject(@Valid @RequestBody ProjectPatchReq projectPatchReq) {
        projectService.patchProject(projectPatchReq);
    }

    @GetMapping(UrlUtils.FILTER)
    public List<ProjectResp> findProjectsWithFilters(@RequestParam(required = false) String searchName,
                                                     @RequestParam(required = false) ProjectStatus searchStatus) {
        return projectService.findProjectsWithFilters(searchName, searchStatus);
    }

    @GetMapping
    public List<ProjectResp> findProjects() {
        return projectService.findProjects();
    }

    @GetMapping(UrlUtils.ID)
    public ProjectResp findProjectById(@PathVariable("id") @Min(1) Long id) {
        return projectService.findProjectById(id);
    }
}
