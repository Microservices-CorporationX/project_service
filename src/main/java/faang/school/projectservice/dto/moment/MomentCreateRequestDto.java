package faang.school.projectservice.dto.moment;

import faang.school.projectservice.utils.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MomentCreateRequestDto(
        @NotBlank
        String name,
        String description,
        @DateTimeFormat(pattern = Constants.DATE_FORMAT)
        LocalDateTime date,
        @NotNull
        List<Long> projectIds,
        List<Long> teamMemberIds
) {
}