package faang.school.projectservice.dto.teamMember;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateTeamMemberDto(
        String username,
        List<TeamRole> roles,
        List<Long> stageIds
) {
}
