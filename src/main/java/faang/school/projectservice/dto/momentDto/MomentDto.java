package faang.school.projectservice.dto.momentDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;
    private String date;

    @NotNull
    private List<Long> projectIds;

    @NotNull
    private List<Long> userIds;

    private String createdAt;
    private String updatedAt;
    private Long createdBy;
}
