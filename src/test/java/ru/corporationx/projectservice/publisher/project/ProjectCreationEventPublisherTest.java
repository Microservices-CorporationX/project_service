package ru.corporationx.projectservice.publisher.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import ru.corporationx.projectservice.model.dto.project.ProjectEvent;
import ru.corporationx.projectservice.publisher.project.ProjectCreationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectCreationEventPublisherTest {
    @Value("${spring.data.redis.channels.project-channel}")
    private String topic;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ProjectCreationEventPublisher projectCreationPublisher;

    @Test
    public void testSuccessfulPublish() throws JsonProcessingException {
        String message = "test message";
        ProjectEvent projectEvent = new ProjectEvent();
        when(objectMapper.writeValueAsString(projectEvent)).thenReturn(message);

        projectCreationPublisher.publish(projectEvent);

        verify(redisTemplate).convertAndSend(topic, message);
    }

    @Test
    public void testPublishWithJsonProcessingException() throws JsonProcessingException {
        ProjectEvent event = new ProjectEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> projectCreationPublisher.publish(event));
    }
}