package faang.school.projectservice.event.project;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubProjectClosedEvent extends ApplicationEvent {

    private final Long projectId;

    public SubProjectClosedEvent(Object source, Long projectId) {
        super(source);
        this.projectId = projectId;
    }
}
