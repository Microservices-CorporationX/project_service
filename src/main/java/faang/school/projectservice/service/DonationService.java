package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.spetification.DonationSpecification;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final CampaignService campaignService;
    private final UserService userService;
    private final DonationSpecification donationSpecification;


    public DonationDto createDonation(DonationDto dto) {
        PaymentRequest paymentRequest = mapDonationToPaymentRequest(dto);
        paymentServiceClient.sendPayment(paymentRequest);
        Donation entity = donationMapper.toEntity(dto);
        donationRepository.save(entity);
        return dto;
    }

    public DonationDto findDonationByIdAndUserId(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new DataValidationException("Ошибка валидации метода findDonationByIdAndUserId");
        }
        findUserById(userId);
        var donation = donationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Донат с ID " + id + " не найден "));
        return donationMapper.toDto(donation);
    }

    public List<DonationDto> getDonationByIdUser(long userId, DonationFilter donationFilter) {
        findUserById(userId);
        Specification<Donation> donationSpec = donationSpecification.build(userId, donationFilter);
        List<Donation> donations = donationRepository.findAll(donationSpec);
        return donations.stream().map(donationMapper::toDto).toList();
    }

    private PaymentRequest mapDonationToPaymentRequest(DonationDto dto) {
        Campaign campaign = findCampaignById(dto.campaignId());
        validCampaignStatus(campaign);
        return PaymentRequest.builder()
                .paymentNumber(dto.paymentNumber())
                .amount(dto.amount())
                .paymentCurrency(campaign.getCurrency())
                .targetCurrency(dto.currency())
                .build();
    }

    private Campaign findCampaignById(long idCampaign) {
        return campaignService.findById(idCampaign);
    }

    private void validCampaignStatus(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BusinessException("Ошибка статуса компании " + campaign.getStatus());
        }
    }

    private void findUserById(long userId) {
        userService.getUserDtoById(userId);
    }

}
