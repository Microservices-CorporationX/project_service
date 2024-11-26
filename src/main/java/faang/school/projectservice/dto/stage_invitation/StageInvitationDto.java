package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationDto {
    @NotNull
    @PositiveOrZero
    private Long id;

    private StageInvitationStatus status;

    @NotNull
    @PositiveOrZero
    private Long stageId;

    @NotNull
    @PositiveOrZero
    private Long authorId;

    @NotNull
    @PositiveOrZero
    private Long invitedId;
}
