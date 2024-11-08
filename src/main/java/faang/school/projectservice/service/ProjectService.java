package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public boolean projectIsOpen(Long projectId) {
        ProjectStatus status = getProjectById(projectId).getStatus();
        return status == ProjectStatus.CREATED || status == ProjectStatus.IN_PROGRESS;
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
