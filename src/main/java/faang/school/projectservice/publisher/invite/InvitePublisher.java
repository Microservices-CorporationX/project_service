package faang.school.projectservice.publisher.invite;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.invitation.InviteSentEvent;
import faang.school.projectservice.publisher.AbstractPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

public class InvitePublisher extends AbstractPublisher<InviteSentEvent> {

    public InvitePublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public void setChannel(@Value("${spring.data.redis.channels.invitation_channel}") String channel) {
        super.setChannel(channel);
    }
}
