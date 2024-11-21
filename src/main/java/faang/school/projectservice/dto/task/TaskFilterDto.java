package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

@Builder
public record TaskFilterDto(
        TaskStatus status,
        Long performerUserId,
        String keyword
) {
}
