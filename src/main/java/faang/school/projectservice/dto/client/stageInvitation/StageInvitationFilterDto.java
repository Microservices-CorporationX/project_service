package faang.school.projectservice.dto.client.stageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationFilterDto {
    private Long id;
    private String stageName;
    private StageInvitationStatus status;
}
