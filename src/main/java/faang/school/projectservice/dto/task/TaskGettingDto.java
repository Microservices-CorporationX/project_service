package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;

public record TaskGettingDto(
        TaskStatus status,
        Long performerUserId,
        String word
) {}
