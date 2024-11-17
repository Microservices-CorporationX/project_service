package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageInvitationDTO {

    private  Long id;
    @NotBlank
    private String description;
    private StageInvitationStatus status;
    @Positive
    @NotNull(message = "Идентификатор этапа не может быть null")
    private Long stageId;
    @Positive
    @NotNull(message = "Идентификатор автора не может быть null")
    private Long authorId;
    @Positive
    @NotNull(message = "Идентификатор приглашенного не может быть null")
    private Long invitedId;
}
