package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipEditDto {
    @Positive
    @NotNull
    private Long id;

    @Positive
    @NotNull
    private Long projectId;

    @Positive
    @NotNull
    private Long mentorId;

    @NotNull
    private List<Long> internsIds;

    @NotNull
    private TeamRole targetRole;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
}
