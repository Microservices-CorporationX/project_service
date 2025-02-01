package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentServiceValidator {

    private final ProjectService projectService;

    public void validateMomentName(String momentName) {
        if (StringUtils.isBlank(momentName)) {
            log.error("Moment cannot be with empty name!");
            throw new IllegalArgumentException("Moment cannot be with empty name!");
        }
    }

    public void validateMomentProjectIds(List<Long> projectIds) {
        if (projectIds != null && !projectIds.isEmpty()) {
            List<ProjectResponseDto> associatedProjects = projectService.getProjectsByIds(projectIds);
            int activeProjectsListSize = associatedProjects.stream()
                    .filter(project -> ProjectStatus.IN_PROGRESS.toString().equals(project.status()))
                    .toList()
                    .size();
            if (activeProjectsListSize == 0) {
                log.error("Created moment must have at least one active project!");
                throw new IllegalArgumentException("Moment must have at least one active project!");
            }
        }
    }
}
