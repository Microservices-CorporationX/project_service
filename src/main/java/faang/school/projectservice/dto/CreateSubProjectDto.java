package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateSubProjectDto(
        Long id,
        @NotBlank String name,
        @NotNull @Positive Long ownerId,
        @NotNull ProjectVisibility visibility,
        List<StageDto> stages
) {
}
