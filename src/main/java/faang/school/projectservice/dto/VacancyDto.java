package faang.school.projectservice.dto;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyDto(
        Long id,
        @NotBlank String name,
        @NotBlank String description,
        @Positive Long projectId,
        List<Long> candidatesIds,
        @NotBlank VacancyStatus status,
        @NotNull Long createdBy,
        @NotNull Integer count
) {
}
