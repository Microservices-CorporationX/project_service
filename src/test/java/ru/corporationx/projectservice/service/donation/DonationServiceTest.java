package ru.corporationx.projectservice.service.donation;

import ru.corporationx.projectservice.client.PaymentServiceClient;
import ru.corporationx.projectservice.client.UserServiceClient;
import ru.corporationx.projectservice.config.context.UserContext;
import ru.corporationx.projectservice.mapper.donation.DonationMapper;
import ru.corporationx.projectservice.mapper.donation.FundRaisedEventMapper;
import ru.corporationx.projectservice.model.dto.client.Currency;
import ru.corporationx.projectservice.model.dto.client.PaymentRequest;
import ru.corporationx.projectservice.model.dto.client.UserDto;
import ru.corporationx.projectservice.model.dto.donation.DonationDto;
import ru.corporationx.projectservice.model.entity.Campaign;
import ru.corporationx.projectservice.model.entity.CampaignStatus;
import ru.corporationx.projectservice.model.entity.Donation;
import ru.corporationx.projectservice.model.entity.Project;
import ru.corporationx.projectservice.publisher.fund.FundRaisedEventPublisher;
import ru.corporationx.projectservice.repository.DonationRepository;
import ru.corporationx.projectservice.service.campaign.CampaignService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {
    public static final long USER_ID = 1L;

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private DonationMapper donationMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private CampaignService campaignService;
    @Mock
    private UserContext userContext;
    @Mock
    private FundRaisedEventMapper fundRaisedEventMapper;
    @Mock
    private FundRaisedEventPublisher fundRaisedEventPublisher;
    @InjectMocks
    private DonationService donationService;

    @Test
    void whenPaymentClientThrowsFeignException_thenWrapAsRuntimeException() {
        DonationDto donationDto = getDonationDto();
        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency());
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.ACTIVE);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(new UserDto());
        when(campaignService.getCampaignById(donationDto.getCampaignId())).thenReturn(campaign);
        when(paymentServiceClient.sendPayment(paymentRequest)).thenThrow(mock(FeignException.class));

        assertThrows(RuntimeException.class, () -> donationService.saveDonation(donationDto));
    }

    @Test
    void whenCampaignStatusIsInactive_thenThrowIllegalArgumentException() {
        DonationDto donationDto = getDonationDto();
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.COMPLETED);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(new UserDto());
        when(campaignService.getCampaignById(donationDto.getCampaignId())).thenReturn(campaign);

        assertThrows(IllegalArgumentException.class, () -> donationService.saveDonation(donationDto));
    }

    @Test
    void whenUserServiceClientThrowsFeignException_thenWrapAsIllegalArgumentException() {
        DonationDto donationDto = getDonationDto();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenThrow(mock(FeignException.class));

        assertThrows(IllegalArgumentException.class, () -> donationService.saveDonation(donationDto));
    }

    @Test
    void whenSaveDonationCalledWithValidData_thenRepositorySaveIsCalled() {
        DonationDto donationDto = getDonationDto();
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setProject(new Project());
        Donation donation = Donation.builder()
                .userId(USER_ID)
                .campaign(campaign)
                .amount(BigDecimal.valueOf(1L))
                .build();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userServiceClient.getUser(USER_ID)).thenReturn(new UserDto());
        when(campaignService.getCampaignById(donationDto.getCampaignId())).thenReturn(campaign);
        when(donationMapper.toEntity(donationDto)).thenReturn(donation);

        donationService.saveDonation(donationDto);

        verify(donationRepository).save(donation);
        verify(fundRaisedEventPublisher).publish(any());
        verify(donationMapper).toDto(donation);
    }

    private static DonationDto getDonationDto() {
        return DonationDto.builder()
                .campaignId(1L)
                .paymentNumber(1L)
                .amount(BigDecimal.valueOf(1L))
                .currency(Currency.USD)
                .build();
    }
}