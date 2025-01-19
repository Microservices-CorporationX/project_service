package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipCreateDto {
    @Positive
    @NotNull
    private Long projectId;

    @Positive
    @NotNull
    private Long mentorId;

    @NotNull
    private List<Long> internsIds;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
}
