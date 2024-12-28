package faang.school.projectservice.dto.teammember;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDto {
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private List<TeamRole> roles;

    private Long teamId;
}
