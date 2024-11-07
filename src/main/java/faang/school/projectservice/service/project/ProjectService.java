package faang.school.projectservice.service.project;

import faang.school.projectservice.jpa.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectRepository;

    public boolean isProjectExists(long projectId) {
        return projectRepository.existsById(projectId);
    }
}