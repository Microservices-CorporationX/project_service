package faang.school.projectservice.dto.momentDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentDto {
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;
    private LocalDateTime date;

    @NotNull
    private List<Long> projectIds;

    @NotNull
    private List<Long> userIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
}
