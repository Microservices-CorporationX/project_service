package faang.school.projectservice.dto.stageinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageInvitationDto {

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Long stageId;

    @NotNull
    @Positive
    private Long authorId;

    @NotNull
    @Positive
    private Long invitedId;
}
