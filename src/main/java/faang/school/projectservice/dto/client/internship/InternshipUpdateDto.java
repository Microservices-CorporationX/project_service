package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class InternshipUpdateDto {

    @NotNull
    private Long internshipId;

    @NotNull
    private TeamRole internNewTeamRole;
}
