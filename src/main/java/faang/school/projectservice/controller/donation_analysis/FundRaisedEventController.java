package faang.school.projectservice.controller.donation_analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.service.donation_analysis.FundRaisedEventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/donation-analysis")
@AllArgsConstructor
@Validated
public class FundRaisedEventController {
    private final FundRaisedEventService eventService;

    @PostMapping
    public void processDonation(@RequestBody @Valid Donation donation) throws JsonProcessingException {
        eventService.processDonation(donation);
    }
}
