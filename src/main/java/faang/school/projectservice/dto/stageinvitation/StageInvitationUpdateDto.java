package faang.school.projectservice.dto.stageinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StageInvitationUpdateDto {
    @Positive
    @NotNull
    private final Long id;

    @NotBlank
    private String description;

    @Positive
    @NotNull
    private Long stageId;
}
