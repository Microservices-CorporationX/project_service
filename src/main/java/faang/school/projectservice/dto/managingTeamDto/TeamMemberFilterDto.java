package faang.school.projectservice.dto.managingTeamDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberFilterDto {
    private String name;
    private String role;
}