package faang.school.projectservice.publisher.donation_analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class FundRaisedEventPublisherTest {
    @InjectMocks
    private FundRaisedEventPublisher eventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        FundRaisedEventDto event = FundRaisedEventDto.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(1))
                .donationTime(LocalDateTime.now())
                .build();

        String mockJson = "{\"userId\":1,\"amount\":100.0,\"donationTime\":null}";
        when(objectMapper.writeValueAsString(event)).thenReturn(mockJson);

        eventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(null, mockJson);
        verify(objectMapper, times(1)).writeValueAsString(event);
    }
}
