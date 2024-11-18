package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateTeamMemberDto(
        @Positive Long id,
        @Positive Long userId,
        String username,
        List<TeamRole> roles,
        Long teamId,
        List<Long> stageIds
) {
}
