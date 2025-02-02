package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donations")
public class DonationController {
    private final DonationService donationService;
    private final UserContext userContext;

    @PostMapping("/send")
    public DonationDto sendDonation(@Valid @RequestBody DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/user/{donationId}")
    public DonationDto getDonationsByUserId(@PathVariable Long donationId) {
        return donationService.getDonationByUserId(donationId, userContext.getUserId());
    }

    @GetMapping("/filter/{userId}")
    public List<DonationDto> getDonationsByFilter(@RequestBody DonationFilterDto filter, @PathVariable Long userId) {
        return donationService.getDonationsByFilter(filter, userId);
    }
}
