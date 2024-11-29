package faang.school.projectservice.validator.teamMember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    public static final Set<TeamRole> ALLOWED_ROLES = Set.of(TeamRole.OWNER, TeamRole.MANAGER);
    private final TeamMemberService teamMemberService;

    public void validateUserRoleForPublishing(Long userId, Long projectId) {
        TeamMember teamMember = teamMemberService.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User with id - " + userId +
                        " is not a team member of project with id - " + projectId));

        if (!hasRequiredRoles(teamMember.getRoles())) {
            throw new DataValidationException("Only team member with the role of MANAGER or OWNER " +
                    "can create fundraising activities for project!");
        }
    }

    private boolean hasRequiredRoles(List<TeamRole> roles) {
        return roles.stream().anyMatch(ALLOWED_ROLES::contains);
    }
}
