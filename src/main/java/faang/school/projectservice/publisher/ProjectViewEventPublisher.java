package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.events.ProjectViewEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher extends AbstractEventPublisher<ProjectViewEvent>{
    public ProjectViewEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ChannelTopic projectViewEventChannel,
            ObjectMapper objectMapper
    ) {
        super(redisTemplate, projectViewEventChannel, objectMapper);
    }

    @Override
    public Class<ProjectViewEvent> getInstance() {
        return ProjectViewEvent.class;
    }
}
