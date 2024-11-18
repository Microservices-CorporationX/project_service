package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String name;

    private String description;

    private LocalDateTime date;

    @NotNull
    private List<Long> projectIds;

    @NotNull
    private List<Long> userIds;

}
