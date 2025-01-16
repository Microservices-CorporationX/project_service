package faang.school.projectservice.dto.moment;

import java.util.List;

public record MomentDto(
        Long id,
        String name,
        String description,
        String date,
        List<Integer> projectIds
) {
}
