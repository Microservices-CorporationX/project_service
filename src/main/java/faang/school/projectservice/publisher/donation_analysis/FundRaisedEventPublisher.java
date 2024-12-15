package faang.school.projectservice.publisher.donation_analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.exception.EventPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;

@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(FundRaisedEventPublisher.class);

    public void publish(FundRaisedEventDto event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend("fundRaised_topic", json);
            log.info("Published event to Redis: {}", json);
        } catch (JsonProcessingException e) {
            throw new EventPublishingException("Failed to publish event", e);
        }
    }
}

