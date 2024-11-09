package faang.school.projectservice.validator;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectService projectService;

    public boolean isOpenProject(Long projectId) {
        ProjectStatus status = projectService.getProjectById(projectId).getStatus();
        return status == ProjectStatus.CREATED || status == ProjectStatus.IN_PROGRESS;
    }
}
