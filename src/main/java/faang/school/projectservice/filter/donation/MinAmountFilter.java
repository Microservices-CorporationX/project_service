package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.model.Donation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Stream;

@Component
public class MinAmountFilter implements DonationFilter {

    @Override
    public boolean isAcceptable(DonationFilterDto donationFilterDto) {
        return donationFilterDto.minAmount() != null;
    }

    @Override
    public Stream<Donation> accept(Stream<Donation> donations, DonationFilterDto donationFilterDto) {
        return donations.filter(donation -> matchesAmount(donationFilterDto.minAmount(), donation.getAmount()));
    }

    private boolean matchesAmount(Double maxAmount, BigDecimal donationAmount) {
        return donationAmount.doubleValue() >= maxAmount;
    }
}
