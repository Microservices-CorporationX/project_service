package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
    @Positive(message = "Id должно быть больше нуля")
    private long id;

    @Positive(message = "Id должно быть больше нуля")
    private long authorId;

    @Positive(message = "Id должно быть больше нуля")
    private long invitedId;

    @Positive(message = "Id должно быть больше нуля")
    private long stageId;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
}
