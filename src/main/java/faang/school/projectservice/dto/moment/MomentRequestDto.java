package faang.school.projectservice.dto.moment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentRequestDto {
    @NotBlank
    @Schema(description = "Name of the moment")
    private String name;

    @NotBlank
    @Schema(description = "Description of the moment")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Date and time of the moment")
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @NotNull
    @Size(min = 1)
    @Schema(description = "List of project ids", example = "[1, 2, 3]")
    private List<@Positive Long> projectIds;

    @NotNull
    @Size(min = 1)
    @Schema(description = "List of user ids", example = "[1, 2, 3]")
    private List<@Positive Long> userIds;
}
