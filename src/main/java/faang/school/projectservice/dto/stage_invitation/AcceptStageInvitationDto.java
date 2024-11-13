package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptStageInvitationDto {
    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long id;
}
