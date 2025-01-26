package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record UpdateTaskDto(
        @NotEmpty String description,
        TaskStatus status,
        @Positive Long performerUserId,
        @Positive Long parentTaskId,
        List<Long> linkedTaskIds
) {}