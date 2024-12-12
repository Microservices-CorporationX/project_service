package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.FundRaisedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FundRaisedEventPublisher implements RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic donateTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(FundRaisedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(donateTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("Error serializing FundRaisedEvent to json", e);
            throw new RuntimeException(e);
        }
    }
}
