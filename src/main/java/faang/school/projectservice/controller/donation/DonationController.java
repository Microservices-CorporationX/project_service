package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.DonationRequest;
import faang.school.projectservice.dto.FundRaisedEvent;
import faang.school.projectservice.service.donation.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {
    private final DonationService donationService;

    @PostMapping("/donate")
    public ResponseEntity<FundRaisedEvent> donate(@RequestBody DonationRequest donationRequest) {
        FundRaisedEvent event = donationService.donate(donationRequest);

        return ResponseEntity.ok(event);
    }
}
