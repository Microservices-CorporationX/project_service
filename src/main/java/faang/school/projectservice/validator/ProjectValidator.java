package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
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
            throw new NotUniqueProjectException("Project '" + name + "' with ownerId #" + ownerId + " already exists.");
        }

        log.info("Project '{}' with ownerId #{} unique and can be created.", name, ownerId);
    }

    public void validateProjectDescriptionUpdatable(UpdateProjectDto projectDto, Project project) {
        if (projectDto.getDescription().equals(project.getDescription())) {
            log.error("Project #{} can't be updated. Current description is the same.", projectDto.getId());
            throw new DataValidationException("Project #" + projectDto.getId() +
                    " can't be updated. Current description is the same.");
        }
    }

    public void validateProjectStatusUpdatable(UpdateProjectDto projectDto, Project project) {
        if (projectDto.getStatus() == null) {
            log.error("Project #{} can't be updated to null status.", projectDto.getId());
            throw new DataValidationException("Project #" + projectDto.getId() + " can't be updated to null status.");
        }

        if (projectDto.getStatus() == project.getStatus()) {
            log.error("Project #{} can't be updated. Current status is the same.", projectDto.getId());
            throw new DataValidationException("Project #" + projectDto.getId() +
                    " can't be updated. Current status is the same.");
        }
    }

    public void validateProjectVisibilityUpdatable(UpdateProjectDto projectDto, Project project) {
        if (projectDto.getVisibility() == null) {
            log.error("Project #{} can't be updated to null visibility.", projectDto.getId());
            throw new DataValidationException("Project #" + projectDto.getId() +
                    " can't be updated to null visibility.");
        }

        if (projectDto.getVisibility() == project.getVisibility()) {
            log.error("Project #{} can't be updated. Current visibility is the same.", projectDto.getId());
            throw new DataValidationException("Project #" + projectDto.getId() +
                    " can't be updated. Current visibility is the same.");
        }
    }
}