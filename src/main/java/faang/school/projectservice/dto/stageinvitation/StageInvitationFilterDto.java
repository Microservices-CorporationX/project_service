package faang.school.projectservice.dto.stageinvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationFilterDto {

    private String descriptionPattern;
    private StageInvitationStatus status;

    @Positive
    private Long stageId;

    @Positive
    private Long authorId;

    @Positive
    private Long invitedId;
}
