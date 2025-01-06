package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.dto.donation.FilterDonationDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/donations")
public class DonationV1Controller {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponseDto sendDonation(@RequestBody @Valid DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/{id}")
    public DonationResponseDto getDonation(@PathVariable @Positive long id) {
        return donationService.getDonation(id);
    }

    @PostMapping("/retrieve")
    public List<DonationResponseDto> getFilteredDonations(@RequestBody FilterDonationDto filterDonationDto) {
        return donationService.getFilteredDonations(filterDonationDto);
    }
}
