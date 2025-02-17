package ru.corporationx.projectservice.controller.donation;

import ru.corporationx.projectservice.model.dto.donation.DonationDto;
import ru.corporationx.projectservice.service.donation.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {
    private final DonationService donationService;

    @PostMapping
    public DonationDto sendDonation(@Valid @RequestBody DonationDto donationDto) {
        return donationService.saveDonation(donationDto);
    }
}
