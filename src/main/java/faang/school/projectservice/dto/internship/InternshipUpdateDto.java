package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InternshipUpdateDto {

    @NotNull
    private Long internshipId;

    @NotNull
    private TeamRole internNewTeamRole;
}
