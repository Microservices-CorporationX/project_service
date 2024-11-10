package faang.school.projectservice.dto.team_member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class TeamMemberDto {
    private Long id;
    private Long userId;
}
