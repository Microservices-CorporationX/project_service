package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.DonationRequest;
import faang.school.projectservice.dto.FundRaisedEvent;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donations")
public class DonationController {
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    @PostMapping("/donate")
    public ResponseEntity<String> donate(@RequestBody DonationRequest donationRequest) {
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
