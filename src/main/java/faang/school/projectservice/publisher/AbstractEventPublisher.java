package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    public void publish(T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("JSON processing error " + e);
            throw new RuntimeException(e);
        }
    }

    public abstract Class<T> getInstance();
}
