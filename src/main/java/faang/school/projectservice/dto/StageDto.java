package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record StageDto (
        Long id,
        @NotBlank String stageName
) {
}
