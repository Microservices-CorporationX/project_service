package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;

import java.time.LocalDateTime;
import java.util.List;

public class InternshipReadDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internsIds;
    private TeamRole targetRole;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
}
