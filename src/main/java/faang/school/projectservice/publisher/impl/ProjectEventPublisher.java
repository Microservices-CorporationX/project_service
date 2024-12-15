package faang.school.projectservice.publisher.impl;

import faang.school.projectservice.event.ProjectEvent;
import faang.school.projectservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventPublisher implements MessagePublisher<ProjectEvent> {

    @Value("${spring.data.redis.channel.project}")
    private String projectChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(ProjectEvent message) {
        redisTemplate.convertAndSend(projectChannel, message);
        log.info("Published project event - {}", message);
    }
}
