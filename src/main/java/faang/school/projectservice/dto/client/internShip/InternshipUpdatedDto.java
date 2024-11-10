package faang.school.projectservice.dto.client.internShip;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InternshipUpdatedDto {
    @PositiveOrZero
    private Long id;

    private Long projectId;

    @NotNull(message = "Mentor can not be null")
    @Positive(message = "Mentor id must be positive")
    private TeamMember mentorId;

    @NotNull(message = "Start date cannot be null")
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    @NotNull(message = "Start date can not be null")
    private LocalDateTime endDate;

    @NotNull(message = "Internship status can not be null")
    private InternshipStatus status;

    @NotNull(message = "Internship owner cannot be null")
    private Long createdBy;

    private List<@Positive Long> interns;

    private List<@Positive Long> internToDismissal;
}
