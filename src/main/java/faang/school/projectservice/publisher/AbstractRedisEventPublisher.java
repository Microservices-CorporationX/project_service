package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRedisEventPublisher<T> implements MessagePublish<T> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    protected abstract String getChannelName();

    @Override
    public void publish(T event) {
        log.info("Attempting to publish event to channel '{}'. Event: {}", getChannelName(), event);
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(getChannelName(), message);
            log.info("Successfully published event to channel '{}'. Message: {}", getChannelName(), message);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error. Event: {}. Error: {}", event, e.getMessage(), e);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure while publishing event to channel '{}'. Event: {}. Error: {}",
                    getChannelName(), event, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while publishing event to channel '{}'. Event: {}. Error: {}",
                    getChannelName(), event, e.getMessage(), e);
        }
    }
}