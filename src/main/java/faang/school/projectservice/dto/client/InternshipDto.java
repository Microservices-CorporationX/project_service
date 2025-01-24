package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipDto {
    private Long id;
    @NotNull private Long projectId;
    @NotNull private Long mentorId;
    @NotEmpty private List<Long> internIds;
    @NotNull private LocalDateTime startDate;
    @NotNull private LocalDateTime endDate;
    @NotNull private InternshipStatus status;
    @NotNull private TeamRole role;
}
