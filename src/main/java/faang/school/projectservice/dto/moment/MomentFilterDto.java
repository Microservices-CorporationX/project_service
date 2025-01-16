package faang.school.projectservice.dto.moment;

import java.util.List;

public record MomentFilterDto(
        String dateFrom,
        String dateTo,
        List<String> projectsIds
) {
}
