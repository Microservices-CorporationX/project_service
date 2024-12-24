package faang.school.projectservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopicFactory {
    @Value("$spring.data.redis.topic.donation")
    private String donationTopic;

    @Bean
    public ChannelTopic donationTopic() {
        return new ChannelTopic(donationTopic);
    }
}
