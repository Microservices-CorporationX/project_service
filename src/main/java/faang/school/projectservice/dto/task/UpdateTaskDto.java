package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateTaskDto(
        @NotEmpty String description,
        TaskStatus status,
        @Positive Long performerUserId,
        @Positive Long parentTaskId,
        List<Long> linkedTaskIds
) {}