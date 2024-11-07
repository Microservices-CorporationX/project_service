package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectJpaRepository projectRepository;

    public boolean isProjectExists(long projectId) {
        return projectRepository.existsById(projectId);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                        () -> new DataValidationException(
                                String.format("There is no project with such ID (%d) in database!", projectId))
        );
    }
}
