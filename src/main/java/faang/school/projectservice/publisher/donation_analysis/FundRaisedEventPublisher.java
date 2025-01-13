package faang.school.projectservice.publisher.donation_analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;
import faang.school.projectservice.publisher.AbstractRedisEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class FundRaisedEventPublisher extends AbstractRedisEventPublisher<FundRaisedEventDto> {
    @Value("${spring.data.redis.channels.calculations_channel.name}")
    private String calculations_channel;

    public FundRaisedEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    protected String getChannelName() {
        return calculations_channel;
    }
}