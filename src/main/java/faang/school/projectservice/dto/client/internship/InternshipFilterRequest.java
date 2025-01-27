package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.util.List;

@Builder
public record InternshipFilterRequest(List<TeamRole> roles, InternshipStatus status) {

}
