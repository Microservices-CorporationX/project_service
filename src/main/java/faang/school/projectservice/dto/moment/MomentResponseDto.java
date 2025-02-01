package faang.school.projectservice.dto.moment;

import faang.school.projectservice.utils.Constants;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentResponseDto(
        Long id,
        String name,
        String description,
        @DateTimeFormat(pattern = Constants.DATE_FORMAT)
        LocalDateTime date,
        List<Long> projectIds,
        List<Long> teamMembersIds
) {
}
