package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;

    @NotBlank
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    private List<Long> projectIds;

    private String description;

    private LocalDateTime date = LocalDateTime.now();
}
