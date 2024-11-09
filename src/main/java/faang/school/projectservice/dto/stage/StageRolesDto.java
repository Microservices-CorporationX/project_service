package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StageRolesDto {

    @NotNull(message = "Team role is required")
    private TeamRole teamRole;

    @NotNull(message = "Count is required")
    @PositiveOrZero(message = "Count must be positive or zero")
    private Integer count;
}
