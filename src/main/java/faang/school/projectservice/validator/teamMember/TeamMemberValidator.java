package faang.school.projectservice.validator.teamMember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    private final TeamMemberService teamMemberService;

    public void validateUserHasStatusOwnerOrManagerInTeam(Long userId, Long projectId) {
        TeamMember teamMember = teamMemberService.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User with id - " + userId +
                        " is not a team member of project with id - " + projectId));

        if (!teamMember.getRoles().contains(TeamRole.OWNER) && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new DataValidationException("Only team member with the role of MANAGER or OWNER " +
                    "can create fundraising activities for project!");
        }
    }
}
