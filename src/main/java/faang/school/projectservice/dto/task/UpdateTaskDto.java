package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import lombok.Builder;

@Builder
public record UpdateTaskDto(
        String description,
        TaskStatus status,
        Long performerUserId,
        Long parentTaskId,
        Long stageId
) {
}
