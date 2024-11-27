package faang.school.projectservice.validator.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
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
public class ProjectValidator {

    private final UserContext userContext;
    private final ProjectRepository projectRepository;

    public void validateName(String projectName, Long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectName)) {
            log.info("User with id {} tried to create a project with the same name", userContext.getUserId());
            throw new DataValidationException("Can not create new project with this project name, " +
                    "this name is already used for another project of this user");
        }
    }

    public void validateUniqueProject(ProjectDto dto) {
        if (projectRepository.existsById(dto.getId())) {
            log.warn("Project with id {} already exists", dto.getId());
            throw new DataValidationException("Project with id " + dto.getId() + " already exists");
        }
    }

    public void validateUniqueProject(CreateSubProjectDto dto) {
        if (projectRepository.existsById(dto.getId())) {
            log.warn("Project with id {} already exists", dto.getId());
            throw new DataValidationException("Project with id " + dto.getId() + " already exists");
        }
    }

    public void validateIsPublic(Project parent, Project project) {
        if (parent.getVisibility().equals(ProjectVisibility.PUBLIC)
                && project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            log.warn("Cannot create private subproject id: {} for public project id: {}"
                    , project.getId(), parent.getId());
            throw new DataValidationException("Cannot create private subproject for public project");
        }
    }

    public void validateProjectAlreadyCompleted(Project project) {
        if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            log.warn("Project with id {} already completed", project.getId());
            throw new DataValidationException("Project with id " + project.getId() + " already completed");
        }
    }

    public boolean validateAllChildProjectsCompleted(Project project) {
        if (project.getChildren().stream().allMatch(e -> e.getStatus().equals(ProjectStatus.COMPLETED))){
            return true;
        } else {
            log.warn("Project id:{} has unfinished subprojects", project.getId());
            throw new DataValidationException("Project id: " + project.getId() + " has unfinished subprojects");
        }
    }

    public void validateProjectExists(CreateSubProjectDto dto) {
        if (!projectRepository.existsById(dto.getId())) {
            log.warn("Project with id {} does not exist", dto.getId());
            throw new DataValidationException("Project with id " + dto.getId() + " does not exist");
        }
    }

    public boolean needToUpdateVisibility(Project project, CreateSubProjectDto dto) {
        return !project.getVisibility().equals(dto.getVisibility());
    }

    public boolean needToUpdateStatus(Project project, CreateSubProjectDto dto) {
        return !project.getStatus().equals(dto.getStatus());
    }
}
