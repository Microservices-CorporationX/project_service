package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record VacancyDto(
        @Positive Long id,
        @NotBlank String name,
        @Positive Long projectId,
        @NotBlank String description,
        @NotNull List<Long> candidatesIds,
        @NotBlank VacancyStatus status,
        @NotNull Long createdBy,
        @NotNull Integer count
) {
}
