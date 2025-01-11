package faang.school.projectservice.publisher.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.FundRaisedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.fund_raised_channel.name}")
    private String topic;

    public void publish(FundRaisedEvent fundRaisedEvent) {
        try {
            log.info("event publication: {}", fundRaisedEvent);
            String stringValue = objectMapper.writeValueAsString(fundRaisedEvent);
            redisTemplate.convertAndSend(topic, stringValue);
        } catch (JsonProcessingException e) {
            log.error("failed to serialize: {}", fundRaisedEvent, e);
            throw new RuntimeException(e);
        }
    }
}
