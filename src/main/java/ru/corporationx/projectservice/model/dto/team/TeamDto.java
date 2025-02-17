package ru.corporationx.projectservice.model.dto.team;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.corporationx.projectservice.model.dto.teammember.TeamMemberDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    @Positive
    private Long id;

    private List<@NonNull TeamMemberDto> teamMembers;

    @NotNull
    private Long projectId;

    @NotNull
    private Long authorId;
}
