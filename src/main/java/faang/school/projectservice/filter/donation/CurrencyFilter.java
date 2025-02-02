package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public enum CurrencyFilter implements DonationFilter {
    CURRENCY;

    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getCurrencyPattern() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> elements, DonationFilterDto filter) {
        return elements.filter(donation ->
                donation.getCurrency() == filter.getCurrencyPattern());
    }
}
