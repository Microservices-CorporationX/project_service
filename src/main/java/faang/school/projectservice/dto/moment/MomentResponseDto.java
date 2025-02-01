package faang.school.projectservice.dto.moment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentResponseDto(
        Long id,
        String name,
        String description,
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> teamMembersIds
) {
}
