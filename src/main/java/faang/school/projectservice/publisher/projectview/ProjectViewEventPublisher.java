package faang.school.projectservice.publisher.projectview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.AbstractRedisEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjectViewEventPublisher extends AbstractRedisEventPublisher<ProjectViewEvent> {
    @Value("${spring.data.redis.channels.projects_view_channel.name}")
    private String projects_view_channel;

    public ProjectViewEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    protected String getChannelName() {
        return projects_view_channel;
    }
}