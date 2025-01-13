package faang.school.projectservice.publisher.TaskCompletedEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.AbstractRedisEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskCompletedEventPublisher extends AbstractRedisEventPublisher<TaskCompletedEvent> {
    @Value("${spring.data.redis.channels.tasks_view_channel.name}")
    private String tasks_view_channel;

    public TaskCompletedEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    protected String getChannelName() {
        return tasks_view_channel;
    }
}