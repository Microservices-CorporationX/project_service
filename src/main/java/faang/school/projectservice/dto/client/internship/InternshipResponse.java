package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InternshipResponse(long id, long projectId, long mentorId,
                                 List<Long> internIds, LocalDateTime startDate,
                                 LocalDateTime endDate, InternshipStatus status, TeamRole role) {

}
