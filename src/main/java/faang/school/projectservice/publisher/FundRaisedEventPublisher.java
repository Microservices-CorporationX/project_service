package faang.school.projectservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.FundRaisedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class FundRaisedEventPublisher extends AbstractEventPublisher<FundRaisedEvent> {
    public FundRaisedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ChannelTopic donationTopic,
                                    ObjectMapper objectMapper) {
        super(redisTemplate, donationTopic, objectMapper);
    }

    @Override
    public Class<FundRaisedEvent> getInstance() {
        return FundRaisedEvent.class;
    }
}
