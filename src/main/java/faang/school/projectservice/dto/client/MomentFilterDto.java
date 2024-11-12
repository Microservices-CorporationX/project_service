package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.Month;
import java.util.List;

@Builder
@Data
public class MomentFilterDto {

    private Month month;

    @NotEmpty(message = "Project IDs list cannot be empty")
    private List<Long> projectIds;
}
