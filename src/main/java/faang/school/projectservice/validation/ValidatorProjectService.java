package faang.school.projectservice.validation;

import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ValidatorProjectService {

    private final ProjectRepository projectRepository;

    public Project validateProjectExistence(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Проект с ID " + projectId + " не найден")
        );
    }

    public void validateVisibility(Project parentProject, ProjectVisibility subProjectVisibility) {
        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE &&
                subProjectVisibility != ProjectVisibility.PRIVATE) {
            throw new BusinessException("Невозможно создать публичный подпроект для приватного родительского проекта");
        }
    }

    public void validateParentProjectStatus(Project parentProject) {
        if (parentProject.getStatus() == ProjectStatus.CANCELLED) {
            throw new BusinessException("Невозможно создать подпроект для отмененного родительского проекта.");
        }

        if (parentProject.getStatus() == ProjectStatus.COMPLETED) {
            throw new BusinessException("Невозможно создать подпроект для завершенного родительского проекта.");
        }
    }
}
