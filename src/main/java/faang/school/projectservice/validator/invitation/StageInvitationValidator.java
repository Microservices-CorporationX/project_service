package faang.school.projectservice.validator.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.springframework.stereotype.Component;

@Component
public class StageInvitationValidator {

    public void validatePendingStatus(StageInvitation stageInvitation) {
        if (!stageInvitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not in a PENDING state");
        }
    }

    public void validateInvitedId(StageInvitation stageInvitation, Long invitedId) {
        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("Invited ID does not match the expected value");
        }
    }

    public void validateRejectDescription(String rejectDescription) {
        if (rejectDescription == null && rejectDescription.isEmpty()) {
            throw new IllegalArgumentException("description не может быть равен null или быть пустым");
        }
    }
}
