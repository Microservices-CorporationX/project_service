package faang.school.projectservice.service.impl;

import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceValidator {
    private final ProjectService projectService;

    void validateUserInProject(Long userId, Long projectId) {
        if (!isUserInProject(userId, projectId)) {
            throw new IllegalArgumentException("User with id "
                    + userId + " not in project "
                    + projectId + " at this moment");
        }
    }

    void validateUserCanDownloadResource(Long userId, Long projectId) {
        boolean isProjectNotPublic = !projectService.isProjectPublic(projectId);
        boolean isUserNotInProject = !projectService.isUserInProject(userId, projectId);
        if (isUserNotInProject || isProjectNotPublic) {
            throw new IllegalArgumentException("User with id "
                    + userId + " has not access to resources of project "
                    + projectId + " at this moment");
        }
    }

    void validateResourcesOversize(Long projectId) {
        int maxResourcesPerProject = 50;
        if (projectService.getProjectResourceIds(projectId).size() > maxResourcesPerProject) {
            throw new RuntimeException("Limit resources of project is reached [" + maxResourcesPerProject + "]");
        }
    }

    private boolean isUserInProject(Long userId, Long projectId) {
        return projectService.isUserInProject(userId, projectId);
    }

}
