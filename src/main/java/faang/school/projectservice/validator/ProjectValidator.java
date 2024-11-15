package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.NoStatusChangeException;
import faang.school.projectservice.exception.ProjectVisibilityException;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateUniqueProject(ProjectDto dto) {
        Long ownerId = dto.getOwnerId();
        String name = dto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.error("Project '{}' with ownerId #{} already exists.", name, ownerId);
            throw new NotUniqueProjectException(String.format("Project '%s' with ownerId #%d already exists.",
                    name, ownerId));
        }

        log.info("Project '{}' with ownerId #{} unique and can be created.", name, ownerId);
    }

    public void validateUniqueProject(CreateProjectDto dto) {
        Long ownerId = dto.getOwnerId();
        String name = dto.getName();

        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            log.error("SubProject '{}' with ownerId #{} already exists.", name, ownerId);
            throw new NotUniqueProjectException(String.format("Project '%s' with ownerId #%d already exists.",
                    name, ownerId));
        }
        log.info("SubProject '{}' with ownerId #{} unique and can be created.", name, ownerId);
    }

    public boolean canUserAccessProject(Project project, Long currentUserId) {
        return project.getOwnerId().equals(currentUserId) || project.getVisibility() == ProjectVisibility.PUBLIC;
    }

    public void validateProjectExistsById(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project with id %d doesn't exist", projectId));
        }
    }

    public boolean isOpenProject(Long projectId) {
        ProjectStatus status = projectRepository.getProjectById(projectId).getStatus();
        return status == ProjectStatus.CREATED || status == ProjectStatus.IN_PROGRESS;
    }

    public void validateProjectPublic(Project project) {
        if (project.getVisibility() != ProjectVisibility.PUBLIC) {
            throw new ProjectVisibilityException("Only public projects are allowed for this operation");
        }
    }

    public boolean isPublicProject(Project subProject) {
        return subProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    public boolean hasParentProject(Project project) {
        return project.getParentProject() != null;
    }

    public boolean hasChildrenProjects(Project project) {
        return project.getChildren() != null && !project.getChildren().isEmpty();
    }

    public void validateSameProjectStatus(Project project, UpdateSubProjectDto updateSubProjectDto) {
        if (project.getStatus() == updateSubProjectDto.getStatus()) {
            throw new NoStatusChangeException("Project status can't be the same");
        }
    }

    public void validateProjectStatusCompletedOrCancelled(Project project) {
        if (project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.CANCELLED) {
            throw new NoStatusChangeException("Status can't change since project is completed or cancelled");
        }
    }

    public void validateProjectStatusValidToHold(Project project) {
        if (project.getStatus() == ProjectStatus.CREATED) {
            throw new NoStatusChangeException("To hold project it must be in progress first");
        }
    }

    public void validateProjectIsValidToComplete(Project project) {
        if (!project.getChildren().stream().allMatch(child ->
                child.getStatus() == ProjectStatus.COMPLETED) || project.getStatus() == ProjectStatus.CANCELLED) {
            throw new NoStatusChangeException("All subprojects should be completed or cancelled first");
        }
    }

    public void validateCreateSubprojectBasedOnVisibility(Project parentProject, CreateProjectDto projectDto) {
        if (parentProject.getVisibility() != projectDto.getVisibility()) {
            throw new ProjectVisibilityException("The parent project and subproject must have the same visibility");
        }
    }

    public boolean validateHasChildrenProjectsClosed(Project project) {
        return project.getChildren().stream()
                .allMatch(child -> child.getStatus() == ProjectStatus.COMPLETED || child.getStatus() == ProjectStatus.CANCELLED);
    }
}