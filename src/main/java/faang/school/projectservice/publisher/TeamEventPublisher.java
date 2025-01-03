package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.events.TeamEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class TeamEventPublisher extends AbstractEventPublisher<TeamEvent> {
    public TeamEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ChannelTopic projectViewEventChannel,
            ObjectMapper objectMapper
    ) {
        super(redisTemplate, projectViewEventChannel, objectMapper);
    }

    @Override
    public Class<TeamEvent> getInstance(){return TeamEvent.class;}
}
