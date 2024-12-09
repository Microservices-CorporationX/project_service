package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record StageDto (
        Long id,
        @NotBlank String stageName
) {
}
