package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;

    public void validateThatProjectsAreActive(List<Long> projectIds) {
        List<Project> projects = projectService.getProjects(projectIds);
        if (projects.stream().anyMatch(project -> project.getStatus() == ProjectStatus.CANCELLED
                || project.getStatus() == ProjectStatus.ON_HOLD
                || project.getStatus() == ProjectStatus.COMPLETED
        )) {
            throw new BusinessException("Нельзя создать момент для неактивного проекта");
        }
    }

    public void isUserIdsExist(List<Long> userIds) {
        if (userIds != null && !userIds.isEmpty()) {
            teamMemberService.areTeamMembersExist(userIds);
        }
    }

    public void validateMomentUpdateDto(MomentUpdateDto dto) {
        boolean isProjectIdsExist = CollectionUtils.isNotEmpty(dto.getProjectIds());
        boolean isUserIdsExist = CollectionUtils.isNotEmpty(dto.getUserIds());
        if (!isProjectIdsExist && !isUserIdsExist) {
            throw new BusinessException("userIds или projectIds должны быть заполнены");
        }

        if (isProjectIdsExist) {
            validateThatProjectsAreActive(dto.getProjectIds());
        }
        if (isProjectIdsExist) {
            isUserIdsExist(dto.getUserIds());
        }
    }
}
