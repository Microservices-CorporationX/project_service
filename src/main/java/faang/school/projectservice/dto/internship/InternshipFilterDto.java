package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record InternshipFilterDto(
        InternshipStatus status,
        TeamRole role
) {
}
