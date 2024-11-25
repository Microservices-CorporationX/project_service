package faang.school.projectservice.dto.filter;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {
    private Month monthPattern;
    @NotEmpty
    private List<Long> projectIds;
}
