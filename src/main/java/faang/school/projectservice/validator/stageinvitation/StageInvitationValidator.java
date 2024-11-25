package faang.school.projectservice.validator.stageinvitation;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;

public interface StageInvitationValidator {
    StageInvitation getStageInvitation(Long invitationId);

    void validateAuthorAndInvited(Long authorId, Long invitedId);

    void validateInvitationDoesNotExist(TeamMember invited, Stage stage);

    void validateInvitationStatus(StageInvitation stageInvitation, StageInvitationStatus requiredStatus);
}
