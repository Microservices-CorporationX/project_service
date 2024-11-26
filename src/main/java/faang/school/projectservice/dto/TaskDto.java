package faang.school.projectservice.dto;

import faang.school.projectservice.model.TaskStatus;

public class TaskDto {

    private Long id;

    private String name;

    private String description;

    private TaskStatus status;

    private Long performerUserId;

    private Long reporterUserId;
}
