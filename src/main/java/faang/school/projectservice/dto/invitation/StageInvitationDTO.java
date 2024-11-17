package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageInvitationDTO {

    private Long id;
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "Идентификатор этапа не может быть null")
    private Long stageId;
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;
    @NotNull(message = "Идентификатор приглашенного не может быть null")
    private Long invitedId;
}
