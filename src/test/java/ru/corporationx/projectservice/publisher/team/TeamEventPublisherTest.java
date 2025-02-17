package ru.corporationx.projectservice.publisher.team;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import ru.corporationx.projectservice.model.dto.team.TeamEvent;
import ru.corporationx.projectservice.publisher.team.TeamEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TeamEventPublisher publisher;

    @Value("${spring.data.redis.channels.team-channel.name}")
    private String teamChannel;

    @Test
    public void testSuccessfulPublish() throws JsonProcessingException {
        TeamEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(teamChannel, "some_json");
    }

    @Test
    public void testPublishWithJsonProcessingException() throws JsonProcessingException {
        TeamEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    private TeamEvent prepareEvent() {
        return new TeamEvent(1L, 2L, 3L, LocalDateTime.now());
    }
}
