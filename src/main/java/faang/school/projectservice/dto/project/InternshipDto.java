package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internIds;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
}