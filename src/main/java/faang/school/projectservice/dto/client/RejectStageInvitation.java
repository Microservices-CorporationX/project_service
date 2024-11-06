package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectStageInvitation {
    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long id;

    @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
    private String description;
}
