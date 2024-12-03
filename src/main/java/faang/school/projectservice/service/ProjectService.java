package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public boolean isProjectComplete(long id){
        return getProjectById(id).getStatus() == ProjectStatus.COMPLETED;
    }

    public ResponseProjectDto getProject(long projectId) {
        Project project = getProjectById(projectId);
        return projectMapper.toResponseDto(project);
    }
}
