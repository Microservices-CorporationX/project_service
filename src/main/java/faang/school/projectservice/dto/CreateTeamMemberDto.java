package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateTeamMemberDto(
        Long id,
        @Positive @NotNull Long userId,
        @NotBlank List<TeamRole> roles,
        @NotBlank List<Long> stageIds
) {
}
