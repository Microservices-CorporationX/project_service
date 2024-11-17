package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;

public class StageInvitationDTO {

    private Long id;
    @NotNull(message = "Description is required")
    private String description;
    private StageInvitationStatus status;
    private Long stageId;
    private Long authorId;
    private Long invitedId;
}
