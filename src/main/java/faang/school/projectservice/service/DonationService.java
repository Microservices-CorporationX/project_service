package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.dto.donation.FilterDonationDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.specification.DonationSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final DonationMapper donationMapper;
    private final DonationRepository donationRepository;
    private final CampaignService campaignService;
    private final PaymentServiceClient paymentServiceClient;
    private final DonationSpecification donationSpecification;

    @Transactional
    public DonationResponseDto sendDonation(DonationDto donationDto) {
        log.info("Trying to send donation: {}", donationDto);
        Donation donation = donationMapper.toEntity(donationDto);
        donation.setCampaign(campaignService.findCampaignById(donationDto.campaignId()));
        donationRepository.save(donation);
        PaymentRequest paymentRequest = donationMapper.toPaymentRequest(donation);
        paymentServiceClient.sendPayment(paymentRequest);
        return donationMapper.toDonationResponseDto(donation);
    }

    public DonationResponseDto getDonation(long id) {
        log.info("Trying to get donation under id: {}", id);
        return donationMapper.toDonationResponseDto(findDonationById(id));
    }

    public List<DonationResponseDto> getFilteredDonations(FilterDonationDto filterDonationDto) {
        log.info("Trying to get all the donation with the following filters: {}", filterDonationDto);
        Example<Donation> example = Example.of(donationMapper.toEntity(filterDonationDto));
        Specification<Donation> donationSpec = donationSpecification.getSpecByExampleWithAmountRange(example);
        List<Donation> donations = donationRepository.findAll(donationSpec);
        return donationMapper.toDonationsResponseDto(donations);
    }

    private Donation findDonationById(long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Donation under id %d does not exist", id)
                ));
    }
}
