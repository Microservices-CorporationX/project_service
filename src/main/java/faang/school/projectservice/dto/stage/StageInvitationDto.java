package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StageInvitationDto {
    @NotNull
    @Positive(message = "Id должно быть больше нуля")
    private Long id;

    @NotNull
    @Positive(message = "Id должно быть больше нуля")
    private Long authorId;

    @NotNull
    @Positive(message = "Id должно быть больше нуля")
    private Long invitedId;

    @NotNull
    @Positive(message = "Id должно быть больше нуля")
    private Long stageId;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
}
