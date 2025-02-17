package ru.corporationx.projectservice.model.dto.invitation;

import ru.corporationx.projectservice.model.entity.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationFilterDto {
    private String description;
    private StageInvitationStatus status;
    private Long stageId;
    private Long authorId;
}
