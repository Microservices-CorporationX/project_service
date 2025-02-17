package ru.corporationx.projectservice.service.campaign;

import ru.corporationx.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.projectservice.service.campaign.CampaignService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    public static final long CAMPAIGN_ID = 1L;

    @Mock
    private CampaignRepository campaignRepository;
    @InjectMocks
    private CampaignService campaignService;

    @Test
    void testGetCampaignByInvalidId() {
        Mockito.when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> campaignService.getCampaignById(CAMPAIGN_ID));
    }
}