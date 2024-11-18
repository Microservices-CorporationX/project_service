package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InternshipDto(
        Long id,
        @NotBlank String name,
        @NotNull Long projectId,
        InternshipStatus status,
        @NotNull Long mentorId,
        @NotNull List<Long> internIds,
        @Past LocalDateTime startDate,
        @Future LocalDateTime endDate
) {
}
