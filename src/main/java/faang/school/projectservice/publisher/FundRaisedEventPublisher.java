package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.FundRaisedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class FundRaisedEventPublisher implements RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic donateTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Long fundRaisedId) {
        FundRaisedDto fundRaisedDto = new FundRaisedDto();
        fundRaisedDto.setId(fundRaisedId);
        fundRaisedDto.setPaymentAmount(5000L);
        fundRaisedDto.setProjectId(123L);
        fundRaisedDto.setLocalDateTime(LocalDateTime.now());

        try {
            String json = objectMapper.writeValueAsString(fundRaisedDto);
            redisTemplate.convertAndSend(donateTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("json processing error " + e);
            throw new RuntimeException(e);
        }
    }
}
