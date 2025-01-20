package faang.school.projectservice.dto.moment;

import lombok.Builder;

import java.util.List;

@Builder
public record MomentFilterDto(
        //Long momentProject,
        String dateFrom,
        String dateTo,
        List<Long> projectsIds
) {
}
