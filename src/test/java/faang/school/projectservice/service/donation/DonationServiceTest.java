package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.mapper.donation.FundRaisedEventMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.publisher.fund.FundRaisedEventPublisher;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.campaign.CampaignService;
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