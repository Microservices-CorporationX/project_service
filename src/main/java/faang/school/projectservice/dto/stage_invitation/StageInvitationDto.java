package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageInvitationDto {
    @PositiveOrZero
    @NotNull
    private Long id;

    private StageInvitationStatus status;

    @PositiveOrZero
    @NotNull
    private Long stageId;

    @PositiveOrZero
    @NotNull
    private Long authorId;

    @PositiveOrZero
    @NotNull
    private Long invitedId;
}
