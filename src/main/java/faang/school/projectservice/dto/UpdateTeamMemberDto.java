package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateTeamMemberDto(
        @Positive @NotNull Long userId,
        String username,
        List<TeamRole> roles,
        List<Long> stageIds
) {
}
