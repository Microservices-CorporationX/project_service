package faang.school.projectservice.publisher.projectview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProjectViewEventPublisherTest {

    @InjectMocks
    private ProjectViewEventPublisher projectViewEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final static String CHANNEL_NAME = "projects_view_channel";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(projectViewEventPublisher, "projects_view_channel", CHANNEL_NAME);
    }

    @Test
    void testPublish_Positive() throws JsonProcessingException {
        LocalDateTime fixedDate = LocalDateTime.of(2022, 1, 1, 0, 0);
        ProjectViewEvent event = ProjectViewEvent.builder()
                .projectId(1L)
                .userId(5L)
                .createdAt(fixedDate)
                .build();
        String json = "{\"projectId\":1,\"userId\":5,\"createdAt\":\"2022-01-01T00:00:00\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        projectViewEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, json);
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    void testPublish_RedisConnectionFailure_Negative() throws JsonProcessingException {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 12, 14, 12, 0, 0);
        ProjectViewEvent event = ProjectViewEvent.builder()
                .projectId(1L)
                .userId(1L)
                .createdAt(fixedTime)
                .build();
        String expectedMessage = "{\"projectId\":1,\"userId\":1,\"createdAt\":\"2024-12-14T12:00:00\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(expectedMessage);
        doThrow(new RedisConnectionFailureException("Connection Error"))
                .when(redisTemplate)
                .convertAndSend(CHANNEL_NAME, expectedMessage);

        projectViewEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, expectedMessage);
    }

    @Test
    void testPublish_JsonProcessingException_Negative() throws JsonProcessingException {
        ProjectViewEvent event = ProjectViewEvent.builder()
                .projectId(1L)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Error") {});

        projectViewEventPublisher.publish(event);

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void testPublish_RuntimeException_Negative() throws JsonProcessingException {
        LocalDateTime fixedTime = LocalDateTime.of(2024, 12, 14, 12, 0, 0);
        ProjectViewEvent event = ProjectViewEvent.builder()
                .projectId(1L)
                .userId(1L)
                .createdAt(fixedTime)
                .build();
        String expectedMessage = "{\"projectId\":1,\"userId\":1,\"createdAt\":\"2024-12-14T12:00:00\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(expectedMessage);
        doThrow(new RuntimeException("Generic Error"))
                .when(redisTemplate)
                .convertAndSend(CHANNEL_NAME, expectedMessage);

        projectViewEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(CHANNEL_NAME, expectedMessage);
    }
}