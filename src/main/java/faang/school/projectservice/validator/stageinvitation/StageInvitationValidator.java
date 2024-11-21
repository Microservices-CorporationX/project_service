package faang.school.projectservice.validator.stageinvitation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stageinvitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageInvitationValidator {

    public void validateIsInvitationSentToThisTeamMember(long invitedId, StageInvitation stageInvitation) {
        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new DataValidationException("This stage invitation does not belong to this team member");
        }
    }
}
