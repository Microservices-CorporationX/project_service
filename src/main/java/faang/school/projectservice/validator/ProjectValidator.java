package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    public void validateProjectIdNotNull(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Project id required");
        }
    }

    public void doesProjectExist(Optional<Project> project) {
        if (project.isEmpty()) {
            throw new EntityNotFoundException("Project not found");
        }
    }

    public boolean isPublicProject(Project project) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC);
    }
}
