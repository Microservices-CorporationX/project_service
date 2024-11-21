package faang.school.projectservice.dto.momentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentFilterDto {
    private Long id;
    private LocalDateTime date;
    private List<Long> projectIds;
}
