package faang.school.projectservice.service.donation;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.payment.CampaignNotActiveException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.payment.PaymentService;
import faang.school.projectservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class DonationService {
    private final PaymentService paymentService;
    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final List<DonationFilter> donationFilters;

    @Transactional
    public Donation createDonation(Donation donation) {
        long userId = getUserId();

        long campaignId = donation.getCampaign().getId();

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow();

        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new CampaignNotActiveException("Campaign not active by id: " + campaignId);
        }

        PaymentResponse paymentResponse = paymentService.makePayment(
                donation.getAmount(), donation.getCurrency());

        donation.setPaymentNumber(paymentResponse.paymentNumber());
        donation.setCampaign(campaign);
        donation.setUserId(userId);

        return donationRepository.save(donation);
    }

    @Transactional(readOnly = true)
    public Donation getDonationById(long donationId) {
        long userId = getUserId();

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow();

        if (donation.getUserId() != userId) {
            throw new IllegalStateException("The donation by id "
                    + donationId + " does not belong to the user by id " + userId);
        }

        return donation;
    }

    @Transactional(readOnly = true)
    public List<Donation> getAllUserDonations(DonationFilterDto dtoFilters) {
        long userId = getUserId();
        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();
        return filterDonations(donations, dtoFilters).toList();
    }

    private Stream<Donation> filterDonations(Stream<Donation> donations, DonationFilterDto dtoFilters) {
        List<DonationFilter> applicableFilters = donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(dtoFilters))
                .toList();

        return donations.filter(donation ->
                applicableFilters.stream()
                        .allMatch(donationFilter ->
                                donationFilter.apply(donation, dtoFilters)));
    }

    private long getUserId() {
        long userId = userContext.getUserId();

        if (!userService.userExists(userId)) {
            throw new IllegalArgumentException("User not found by id: " + userId);
        }

        return userId;
    }
}
