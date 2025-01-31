package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/v1/donations")
public class DonationController {
    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto send(@RequestBody @Valid DonationDto dto) {
        return donationService.send(dto);
    }

    @GetMapping("/{id}/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public DonationDto findDonationByIdAndUserId(@PathVariable Long id, @PathVariable Long userId) {
        return donationService.findDonationByIdAndUserId(id, userId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DonationDto> getDonationByIdUserWithFilter(@PathVariable Long userId,
                                                           @RequestBody(required = false) @Valid DonationFilter dto) {
        return donationService.getDonationByIdUser(userId, dto);
    }

}
