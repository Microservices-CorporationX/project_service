package faang.school.projectservice.validator.team_member;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {

    public void validateIsTeamMemberParticipantOfProject(TeamMember teamMember, StageInvitation invitation) {
        Project invitationProject = invitation.getStage().getProject();

        boolean isNotParticipant = teamMember.getStages().stream()
                .noneMatch(stage -> stage.getProject().getId().equals(invitationProject.getId()));

        if (isNotParticipant) {
            throw new DataValidationException("This team member is not participant of this project");
        }
    }
}
