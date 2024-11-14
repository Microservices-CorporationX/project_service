package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public boolean isMentorPresent(List<Long> memberIds, long mentorId) {
        return memberIds.stream().anyMatch(id -> id == mentorId);
    }
}