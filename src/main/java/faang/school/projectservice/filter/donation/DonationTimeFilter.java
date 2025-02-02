package faang.school.projectservice.filter.donation;

import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.model.Donation;

import java.util.stream.Stream;

public class DonationTimeFilter implements DonationFilter {
    @Override
    public boolean isApplicable(DonationFilterDto filter) {
        return filter.getDonationTimePattern() != null;
    }

    @Override
    public Stream<Donation> apply(Stream<Donation> elements, DonationFilterDto filter) {
        return elements.filter(donation ->
                donation.getDonationTime().isEqual(filter.getDonationTimePattern()));
    }
}
