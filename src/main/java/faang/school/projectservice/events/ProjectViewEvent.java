package faang.school.projectservice.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectViewEvent {
    private final long projectId;
    private final long userId;
    private final LocalDateTime timestamp;
}
