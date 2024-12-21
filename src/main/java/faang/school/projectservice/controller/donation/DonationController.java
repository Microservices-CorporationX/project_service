package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.DonationRequest;
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
    public ResponseEntity<String> donate(@RequestBody DonationRequest donationRequest) {
        boolean isSuccessful = donationService.donate(donationRequest);

        if (isSuccessful) {
            return ResponseEntity.ok("Donation successful");
        } else {
            return ResponseEntity.status(500).body("Donation failed");
        }
    }
}
