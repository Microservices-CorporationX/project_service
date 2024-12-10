package faang.school.projectservice.dto.managingTeamDto;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberFilterDto {
    private String name;
    private TeamRole role;
}