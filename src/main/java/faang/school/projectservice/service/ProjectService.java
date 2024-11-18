package faang.school.projectservice.service;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    private ProjectStatus getProjectStatusById(long id) {
        return projectRepository.getProjectById(id).getStatus();
    }

    public boolean isProjectComplete(long id) {
        return getProjectStatusById(id) == ProjectStatus.COMPLETED;
    }
}
