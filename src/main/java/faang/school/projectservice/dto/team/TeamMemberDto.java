package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDto {
private Long id;
private Long userId;
private List<TeamRole> roles;
private Long teamId;
private LocalDateTime updatedAt;
}
