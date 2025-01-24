package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;

    @NotBlank
    private String name;

    private List<Long> projectIds;

    private String description;

    private LocalDateTime date = LocalDateTime.now();
}
