package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDTO {

    private Long id;
    @NotNull(message = "Идентификатор этапа не может быть null")
    private Long stageId;
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;
    @NotNull(message = "Идентификатор приглашенного не может быть null")
    private Long inviteeId;
    @NotNull(message = "Статус не может быть null")
    private StageInvitationStatus status;
    @Size(max = 255, message = "Причина отклонения слишком длинная")
    private String rejectionReason;
}
