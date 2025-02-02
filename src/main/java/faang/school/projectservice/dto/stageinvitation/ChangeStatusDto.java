package faang.school.projectservice.dto.stageinvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeStatusDto {

    @NotNull
    @Positive
    private Long id;

    @NotNull
    @Positive
    private Long invitedId;
    private StageInvitationStatus status;
}
