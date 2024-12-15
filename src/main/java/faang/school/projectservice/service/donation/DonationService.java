package faang.school.projectservice.service.donation;

import faang.school.projectservice.dto.DonationRequest;
import faang.school.projectservice.dto.FundRaisedEvent;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    public ResponseEntity<String> donate(DonationRequest donationRequest) {
        FundRaisedEvent event = new FundRaisedEvent(
                donationRequest.getUserId(),
                donationRequest.getProjectId(),
                donationRequest.getAmount(),
                LocalDateTime.now()
        );

        fundRaisedEventPublisher.publish(event);
        return ResponseEntity.ok("Donation successful");
    }
}
