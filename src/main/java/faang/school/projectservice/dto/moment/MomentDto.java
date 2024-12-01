package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {

    @Positive
    private Long id;

    @NotNull(message = "Moment name must not be null")
    @NotBlank(message = "Moment name must not be blank")
    private String name;
    @NotNull(message = "Moment description must not be null")
    @NotBlank(message = "Moment description must not be blank")
    private String description;

    private LocalDateTime date;

    @NotEmpty
    private List<@NotNull Long> projectIds;

}
