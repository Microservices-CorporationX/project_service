package faang.school.projectservice.validation;


import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ValidatorSubProjectController {

    private final ProjectRepository projectRepository;

    public void validateParentProjectExists(Long parentProjectId) {
        if (!projectRepository.existsById(parentProjectId)) {
            throw new EntityNotFoundException("Родительский проект с ID " + parentProjectId + " не найден.");
        }
    }

    public void validateParentProjectStatus(Long parentProjectId, ProjectStatus requiredStatus) {
        Project parentProject = projectRepository.findById(parentProjectId).orElseThrow(() ->
                new EntityNotFoundException("Родительский проект с ID " + parentProjectId + " не найден.")
        );

        if (parentProject.getStatus() != requiredStatus) {
            throw new BusinessException("Невозможно создать подпроект для проекта с статусом " + parentProject.getStatus());
        }
    }

    public void validateSubProjectName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Название подпроекта не может быть пустым.");
        }
    }

    public void validateVisibilityForSubProject(Project parentProject, ProjectVisibility subProjectVisibility) {
        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE && subProjectVisibility == ProjectVisibility.PUBLIC) {
            throw new BusinessException("Невозможно создать публичный подпроект для приватного родительского проекта.");
        }
    }
}

