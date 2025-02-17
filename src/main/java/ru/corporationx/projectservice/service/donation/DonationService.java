package ru.corporationx.projectservice.service.donation;

import ru.corporationx.projectservice.client.PaymentServiceClient;
import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.config.context.UserContext;
import ru.corporationx.projectservice.mapper.donation.DonationMapper;
import ru.corporationx.projectservice.mapper.donation.FundRaisedEventMapper;
import ru.corporationx.projectservice.model.dto.client.PaymentRequest;
import ru.corporationx.projectservice.model.dto.client.UserDto;
import ru.corporationx.projectservice.model.dto.donation.DonationDto;
import ru.corporationx.projectservice.model.dto.event.FundRaisedEvent;
import ru.corporationx.projectservice.model.entity.Campaign;
import ru.corporationx.projectservice.model.entity.CampaignStatus;
import ru.corporationx.projectservice.model.entity.Donation;
import ru.corporationx.projectservice.publisher.fund.FundRaisedEventPublisher;
import ru.corporationx.projectservice.repository.DonationRepository;
import ru.corporationx.projectservice.service.campaign.CampaignService;
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
    private final FundRaisedEventPublisher fundRaisedEventPublisher;
    private final FundRaisedEventMapper fundRaisedEventMapper;

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

        publishDonationEvent(entity);

        log.info("successful creation of a donation - amount: {}; currency: {}; userId: {}; campaign: {}",
                donationDto.getAmount(), donationDto.getCurrency(),
                donationDto.getUserId(), donationDto.getCampaignId());
        return donationMapper.toDto(entity);
    }

    private void publishDonationEvent(Donation entity) {
        FundRaisedEvent fundRaisedEvent = fundRaisedEventMapper.donationToFundRaiseEvent(entity);
        fundRaisedEventPublisher.publish(fundRaisedEvent);
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
        if (!CampaignStatus.ACTIVE.equals(campaign.getStatus())) {
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