package faang.school.projectservice.service.donation_analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.publisher.donation_analysis.FundRaisedEventPublisher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Validated
public class FundRaisedEventService {
    private final FundRaisedEventPublisher eventPublisher;

    public void processDonation(Donation donation) throws JsonProcessingException {
        FundRaisedEventDto eventDto = new FundRaisedEventDto
                (donation.getUserId(), donation.getAmount(), donation.getDonationTime());

            eventPublisher.publish(eventDto);
    }
}
