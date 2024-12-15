package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.campaign.CampaignService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final UserServiceClient userServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final CampaignService campaignService;
    private final UserContext userContext;

    public DonationDto saveDonation(DonationDto donationDto) {
        log.info("donation initiation - amount: {}; currency: {}; userId: {}; campaign: {}",
                donationDto.getAmount(), donationDto.getCurrency(),
                donationDto.getUserId(), donationDto.getCampaignId());

        UserDto userDto = validateAndGetUser(userContext.getUserId());
        Campaign campaign = validateAndGetCampaign(donationDto.getCampaignId());
        sendPayment(donationDto);

        Donation entity = donationMapper.toEntity(donationDto);
        entity.setUserId(userDto.getId());
        entity.setCampaign(campaign);
        entity.setDonationTime(LocalDateTime.now());
        donationRepository.save(entity);

        log.info("successful creation of a donation - amount: {}; currency: {}; userId: {}; campaign: {}",
                donationDto.getAmount(), donationDto.getCurrency(),
                donationDto.getUserId(), donationDto.getCampaignId());
        return donationMapper.toDto(entity);
    }

    private void sendPayment(DonationDto donationDto) {
        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency());
        try {
            paymentServiceClient.sendPayment(paymentRequest);
        } catch (FeignException e) {
            throw new RuntimeException("payment failed, try again");
        }
    }

    private Campaign validateAndGetCampaign(Long campaignId) {
        Campaign campaign = campaignService.getCampaignById(campaignId);
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new IllegalArgumentException("campaign is inactive");
        }
        return campaign;
    }

    private UserDto validateAndGetUser(Long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new IllegalArgumentException("user id doesn't exist");
        }
    }
}