package faang.school.projectservice.service.donation;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.DonationRequest;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {
    @InjectMocks
    private DonationService donationService;

    @Mock
    private FundRaisedEventPublisher fundRaisedEventPublisher;

    @Mock
    private UserContext userContext;

    @Test
    void testDonate() {
        DonationRequest donationRequest = new DonationRequest(456L, 150L);

        boolean response = donationService.donate(donationRequest);

        verify(fundRaisedEventPublisher, times(1)).publish(argThat(event ->
                event.getUserId().equals(userContext.getUserId()) &&
                event.getProjectId().equals(donationRequest.getProjectId()) &&
                event.getPaymentAmount().equals(donationRequest.getAmount()) &&
                event.getLocalDateTime() != null
        ));

        assertEquals(true, response);
    }
}
