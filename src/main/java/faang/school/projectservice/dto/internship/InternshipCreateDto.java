package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipCreateDto {
    @Positive
    @NotNull
    private Long projectId;

    @Positive
    @NotNull
    private Long mentorId;

    @NotNull
    private TeamRole role;

    @NotNull
    private List<Long> internsIds;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private InternshipStatus status;
}
