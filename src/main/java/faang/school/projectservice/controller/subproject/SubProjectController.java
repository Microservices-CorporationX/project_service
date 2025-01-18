package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.subproject.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/subproject")
@RestController
public class SubProjectController {

    private final SubProjectService subProjectService;
    private final SubProjectMapper subProjectMapper;

    @PostMapping("/create/{parent-project-id}")
    public ResponseEntity<Void> createSubProject(
            @PathVariable("parent-project-id") final Long parentProjectId,
            @RequestBody @Valid SubProjectDto subProjectDto) {

        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProjectService.createSubProject(parentProjectId, subProject);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{project-id}")
    public ResponseEntity<Void> updateSubProject(
            @PathVariable("project-id") final Long projectId,
            @RequestBody @Valid SubProjectDto subProjectDto) {


        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProjectService.updateSubProject(projectId, subProject);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/subprojects/{parent-project-id}/filters")
    public ResponseEntity<List<SubProjectDto>> getSubProjectsByFilters(
            @PathVariable("parent-project-id") final Long parentProjectId,
            @RequestBody @Valid SubProjectFilterDto filters) {

        List<Project> filteredProjects = subProjectService.getSubProjectsByProjectId(parentProjectId, filters);
        List<SubProjectDto> filteredGoalsDto = subProjectMapper.toDto(filteredProjects);

        return new ResponseEntity<>(filteredGoalsDto, HttpStatus.OK);
    }
}