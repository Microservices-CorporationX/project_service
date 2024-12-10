package faang.school.projectservice.service.donation_analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.publisher.donation_analysis.FundRaisedEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class FundRaisedEventServiceTest {
    @InjectMocks
    private FundRaisedEventService fundRaisedEventService;

    @Mock
    private FundRaisedEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<FundRaisedEventDto> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExceptionProcessDonation() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(eventPublisher).publish(any(FundRaisedEventDto.class));

        Donation fundRaisedEventDto = Donation.builder()
                .amount(BigDecimal.valueOf(1))
                .donationTime(LocalDateTime.now())
                .build();

        assertThrows(RuntimeException.class, () -> fundRaisedEventService.processDonation(fundRaisedEventDto));
        verify(eventPublisher, times(1)).publish(any(FundRaisedEventDto.class));
    }

    @Test
    public void testProcessDonation() throws JsonProcessingException {
        doNothing().when(eventPublisher).publish(any(FundRaisedEventDto.class));

        Donation donation = Donation.builder()
                                    .userId(1L)
                                    .amount(BigDecimal.valueOf(1))
                                    .donationTime(LocalDateTime.now())
                                    .build();

        fundRaisedEventService.processDonation(donation);

        verify(eventPublisher, times(1)).publish(captor.capture());
        FundRaisedEventDto eventDto = captor.getValue();
        assertEquals(eventDto.getUserId(), donation.getUserId());
        assertEquals(eventDto.getAmount(), donation.getAmount());
        assertEquals(eventDto.getDonationTime(), donation.getDonationTime());

    }
}
