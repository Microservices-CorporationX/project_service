package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InternshipDto(
        Long id,
        @NotBlank String name,
        @NotNull TeamRole role,
        @NotNull long projectId,
        @NotNull InternshipStatus status,
        @NotNull long mentorId,
        @NotNull List<Long> internIds,
        @Future LocalDateTime startDate,
        @Future LocalDateTime endDate
) {
}
