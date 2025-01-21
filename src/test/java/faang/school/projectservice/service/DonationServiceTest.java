package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.FilterDonationDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.specification.DonationSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignService campaignService;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private DonationSpecification donationSpecification;

    private BigDecimal amount;
    private long userId;
    private long campaignId;
    private Currency currency;
    private Campaign campaign;
    private Donation donation;
    private long donationId;

    @BeforeEach
    public void setUp() {
        amount = BigDecimal.valueOf(50);
        userId = 1L;
        campaignId = 5L;
        donationId = 5L;
        currency = Currency.USD;
        campaign = Campaign.builder()
                .id(campaignId)
                .build();

        donation = Donation.builder()
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .build();
    }

    @Test
    public void testSendDonation() {
        // arrange
        DonationDto donationDto = DonationDto.builder()
                .userId(userId)
                .amount(amount)
                .campaignId(campaignId)
                .currency(currency)
                .build();

        when(donationMapper.toEntity(donationDto))
                .thenReturn(donation);
        when(campaignService.findCampaignById(campaignId))
                .thenReturn(campaign);

        // act
        ArgumentCaptor<Donation> donationArgumentCaptor = ArgumentCaptor.forClass(Donation.class);
        donationService.sendDonation(donationDto);

        // assert
        verify(donationMapper).toDonationResponseDto(donationArgumentCaptor.capture());
        Donation processedDonation = donationArgumentCaptor.getValue();
        assertEquals(campaign, processedDonation.getCampaign());
    }

    @Test
    public void testGetDonation() {
        // arrange
        donation.setCampaign(campaign);
        when(donationRepository.findById(donationId))
                .thenReturn(Optional.of(donation));

        // act
        donationService.getDonation(donationId);

        // assert
        verify(donationMapper).toDonationResponseDto(donation);
    }

    @Test
    public void testGetDonationNotFound() {
        // arrange
        doThrow(EntityNotFoundException.class)
                .when(donationRepository).findById(donationId);

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonation(donationId));
    }

    @Test
    public void testGetFilteredDonations() {
        BigDecimal maxDonationAmount = BigDecimal.valueOf(100);
        BigDecimal maxDonationAmount1 = BigDecimal.valueOf(10);

        FilterDonationDto filterDonationDto = FilterDonationDto.builder()
                .maxDonationAmount(maxDonationAmount1)
                .maxDonationAmount(maxDonationAmount)
                .currency(currency)
                .build();
        Donation mappedDonation = Donation.builder()
                .currency(currency)
                .maxDonationAmount(maxDonationAmount)
                .minDonationAmount(maxDonationAmount)
                .build();

        Donation firstDonation = Donation.builder()
                .amount(BigDecimal.valueOf(50))
                .currency(currency)
                .build();
        Donation secondDonation = Donation.builder()
                .amount(BigDecimal.valueOf(23))
                .currency(currency)
                .build();

        List<Donation> donations = List.of(
                firstDonation,
                secondDonation
        );

        Specification<Donation> dummySpec = mock(Specification.class);
        ArgumentCaptor<Example<Donation>> exampleArgumentCaptor = ArgumentCaptor.forClass(Example.class);
        when(donationMapper.toEntity(filterDonationDto))
                .thenReturn(mappedDonation);
        when(donationSpecification.getSpecByExampleWithAmountRange(exampleArgumentCaptor.capture()))
                .thenReturn(dummySpec);
        when(donationRepository.findAll(dummySpec))
                .thenReturn(donations);

        // act
        donationService.getFilteredDonations(filterDonationDto);

        // assert
        verify(donationMapper).toDonationsResponseDto(donations);
    }
}
