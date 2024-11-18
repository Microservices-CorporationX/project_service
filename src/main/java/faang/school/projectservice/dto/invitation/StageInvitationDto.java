package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "stageId must not be null")
    private Long stageId;
    @NotNull(message = "authorId must not be null")
    private Long authorId;
    @NotNull(message = "authorId must not be null")
    private Long invitedId;
}
