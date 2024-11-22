package faang.school.projectservice.dto.internship;

import java.time.LocalDateTime;
import java.util.List;

public record InternshipDto(
        Long id,
        String name,
        Long mentorId,
        Long ownedProjectId,
        InternshipStatusDto status,
        RoleDto role,
        List<Long> internIds,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
