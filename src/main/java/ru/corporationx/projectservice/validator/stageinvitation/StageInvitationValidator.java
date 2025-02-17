package ru.corporationx.projectservice.validator.stageinvitation;

import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitation;
import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;

public interface StageInvitationValidator {
    StageInvitation getStageInvitation(Long invitationId);

    void validateAuthorAndInvited(Long authorId, Long invitedId);

    void validateInvitationDoesNotExist(TeamMember invited, Stage stage);

    void validateInvitationStatus(StageInvitation stageInvitation, StageInvitationStatus requiredStatus);
}
