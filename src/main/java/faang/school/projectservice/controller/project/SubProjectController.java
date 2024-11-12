package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    public ResponseEntity<ProjectDto> createProject(ProjectDto projectDto) {
        return ResponseEntity.ok(projectService.createProject(projectDto));
    }

    public ResponseEntity<CreateSubProjectDto> createSubProject(Long parentId, CreateSubProjectDto createSubProjectDto) {
        return ResponseEntity.ok(projectService.createSubProject(parentId, createSubProjectDto));
    }

    public ResponseEntity<CreateSubProjectDto> update(Long id, CreateSubProjectDto dto){
        return ResponseEntity.ok(projectService.update(id, dto));
    }

    public List<CreateSubProjectDto> getProjectsByFilters(ProjectFilterDto filterDto, Long projectId){
        return projectService.getProjectsByFilters(filterDto, projectId);
    }

}
