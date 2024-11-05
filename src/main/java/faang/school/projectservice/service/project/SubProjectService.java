package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;

    public CreateSubProjectDto createSubProject(@NotNull ProjectDto projectDto) {
        Project projectEntity = subProjectMapper.toEntity(projectDto);
        Project parentProject = projectRepository.getProjectById(projectDto.getId());
        projectEntity.setParentProject(parentProject);
        projectEntity.setStatus(ProjectStatus.CREATED);
        Project projectSaved = projectRepository.save(projectEntity);
        parentProject.getChildren().add(projectSaved);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(projectSaved);
    }

    public ProjectDto updateSubProject(ProjectDto projectDto) {
        return null;
    }

    public List<ProjectDto> getSubProjectsByProject(ProjectDto projectDto) {
        return null;
    }

}
