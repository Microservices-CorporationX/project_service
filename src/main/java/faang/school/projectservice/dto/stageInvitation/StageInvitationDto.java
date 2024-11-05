package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;

    @NotNull
    private Long stageId;

    @NotNull
    private Long authorId;

    @NotNull
    private Long invitedId;
}
