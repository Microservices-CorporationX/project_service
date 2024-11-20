package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponseTeamMemberDto(
        @Positive Long id,
        @Positive Long userId,
        @NotBlank List<TeamRole> roles,
        @Positive Long teamId,
        @NotBlank List<@Positive Long> stageIds
) {
}
