package faang.school.projectservice.validator.subproject;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateOwnerExistence(Long ownerId) {
        log.info("Validating owner existence");
    }

    public void validateSubProjectVisibility(ProjectVisibility parentStatus, ProjectVisibility childStatus) {
        if (parentStatus == ProjectVisibility.PRIVATE &&
                childStatus == ProjectVisibility.PUBLIC) {
            log.info("Cannot create public sub project for private parent project");
            throw new IllegalArgumentException("Cannot create public sub project for private parent project");
        }
    }

    public void validateExistenceByOwnerIdAndName(Long ownerId, String name) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.info("User with id = '{}' already has project with name = '{}'", ownerId, name);
            throw new AlreadyExistsException("User already has project with this name");
        }
    }

    public void validateOwnership(Long userId, Long projectId) {
        if (!projectId.equals(userId)) {
            log.info("User with id = '{}' is not owner of project with id = '{}'", userId, projectId);
            throw new IllegalArgumentException("Owner id mismatch");
        }
    }

    public void validateSubProjectBelonging(Long parentId, Project subProject) {
        if (!parentId.equals(subProject.getParentProject().getId())) {
            log.info("Project with id = '{}' does not belong to parent project", subProject.getId());
            throw new IllegalArgumentException("Sub project does not belong to parent project");
        }
    }

    public void validateSubProjectStatus(Project subProject, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.CANCELLED || newStatus == ProjectStatus.COMPLETED) {
            boolean isAnyChildActive = subProject.getChildren().stream()
                    .anyMatch(child ->
                            !child.getStatus().equals(ProjectStatus.CANCELLED) &&
                                    !child.getStatus().equals(ProjectStatus.COMPLETED));
            if (isAnyChildActive) {
                log.info("Cannot change status for sub project with id = '{}'", subProject.getId());
                throw new IllegalArgumentException("Cannot change status for sub project");
            }
        }
    }
}
