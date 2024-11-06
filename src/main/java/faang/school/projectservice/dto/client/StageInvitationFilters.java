package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilters {
    private Long stageId;
    private Long authorId;
    private Long invitedId;
    private StageInvitationStatus status;
}
