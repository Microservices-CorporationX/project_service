package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ResponseProjectDto(
        @Positive long id,
        @NotBlank String name
) {
}
