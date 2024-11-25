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

    public Project getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }

    public boolean isProjectComplete(long id) {
        return getProjectById(id).getStatus() == ProjectStatus.COMPLETED;
    }
}
