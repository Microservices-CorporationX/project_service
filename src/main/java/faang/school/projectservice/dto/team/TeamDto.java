package faang.school.projectservice.dto.team;

import java.util.List;

public record TeamDto(Long id, List<TeamMemberDto> teamMembers) {
}
