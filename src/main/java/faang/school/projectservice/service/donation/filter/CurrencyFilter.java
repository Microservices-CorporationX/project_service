package faang.school.projectservice.service.donation.filter;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CurrencyFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filterDto) {
        return filterDto.getCurrency() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> donationStream, DonationFilterDto filterDto) {
        return donationStream.filter(donation -> donation.getCurrency() == filterDto.getCurrency());
    }
}
