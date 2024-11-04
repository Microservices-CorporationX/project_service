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
        @NotNull @NotBlank String name,
        @NotNull @Positive Long projectId,
        @NotNull @NotBlank String description,
        @NotNull List<Long> candidatesIds,
        @NotNull @NotBlank VacancyStatus status,
        @NotNull Integer count
) {
}
