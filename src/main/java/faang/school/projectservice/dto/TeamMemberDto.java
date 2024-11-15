package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMemberDto {
    private Long userId;
    private TeamRole teamRole;
}
