package faang.school.projectservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.FundRaisedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundRaisedEventPublisherTest {
    public static final String STRING_VALUE = "converted to string object";

    @Value("${spring.data.redis.channels.fund_raised_channel.name}")
    private String topic;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private FundRaisedEventPublisher fundRaisedEventPublisher;

    @Test
    void testSendMethodIsCalled() throws JsonProcessingException {
        FundRaisedEvent fundRaisedEvent = new FundRaisedEvent();
        when(objectMapper.writeValueAsString(fundRaisedEvent)).thenReturn(STRING_VALUE);

        fundRaisedEventPublisher.publish(fundRaisedEvent);

        verify(objectMapper).writeValueAsString(fundRaisedEvent);
        verify(redisTemplate).convertAndSend(topic, STRING_VALUE);
    }

    @Test
    void testJsonProcessingExceptionGetWrappedAsRuntimeException() throws JsonProcessingException {
        FundRaisedEvent fundRaisedEvent = new FundRaisedEvent();
        when(objectMapper.writeValueAsString(fundRaisedEvent)).thenThrow(mock(JsonProcessingException.class));

        Assertions.assertThrows(RuntimeException.class, () -> fundRaisedEventPublisher.publish(fundRaisedEvent));
    }
}