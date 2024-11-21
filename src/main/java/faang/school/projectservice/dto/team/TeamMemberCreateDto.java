package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberCreateDto(
        @Positive(message = "user id can`t be empty")
        long userId,
        List<TeamRole> roles
) {
}
