package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageInvitationFilterDTO {
    @NotNull(message = "Идентификатор приглашенного не может быть null")
    private Long invitedId;
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;
    private StageInvitationStatus status;

}
