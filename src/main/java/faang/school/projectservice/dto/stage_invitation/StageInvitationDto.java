package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
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
