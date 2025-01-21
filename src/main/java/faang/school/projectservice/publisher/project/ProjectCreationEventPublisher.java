package faang.school.projectservice.publisher.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.project.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectCreationEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.project-channel}")
    private String topic;

    public void publish(ProjectEvent projectEvent) {
        try {
            log.info("event publication: {}", projectEvent);
            String stringValue = objectMapper.writeValueAsString(projectEvent);
            redisTemplate.convertAndSend(topic, stringValue);
        } catch (JsonProcessingException e) {
            log.error("failed to serialize: {}", projectEvent, e);
            throw new RuntimeException(e);
        }
    }
}
