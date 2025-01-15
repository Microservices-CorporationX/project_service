package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record StageRolesDto(
        @PositiveOrZero Long id,
        TeamRole teamRole,
        @Positive Integer count
) {
}
