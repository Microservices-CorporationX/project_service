package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;

public record StageInvitationDto(
        Long id,
        String description,
        StageInvitationStatus status,
        @NotNull(message = "stageId must not be null") Long stageId,
        @NotNull(message = "authorId must not be null") Long authorId,
        @NotNull(message = "invitedId must not be null") Long invitedId
) {
}
