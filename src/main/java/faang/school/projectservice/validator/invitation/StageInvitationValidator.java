package faang.school.projectservice.validator.invitation;

import faang.school.projectservice.exception.StageInvitationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationValidator {

    public void validatePendingStatus(StageInvitation stageInvitation) {
        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new StageInvitationException("Invitation is not in a PENDING state");
        }
    }

    public void validateInvitedId(StageInvitation stageInvitation, Long invitedId) {
        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new StageInvitationException("Invited ID does not match the expected value");
        }
    }

    public void validateRejectDescription(String rejectDescription) {
        if (rejectDescription == null && rejectDescription.isEmpty()) {
            throw new StageInvitationException("description не может быть равен null или быть пустым");
        }
    }
}
