package ru.corporationx.projectservice.service.campaign;

import ru.corporationx.projectservice.model.entity.Campaign;
import ru.corporationx.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public Campaign getCampaignById(Long id) {
        log.info("retrieving a campaign by id: {}", id);

        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("campaign doesnt exist"));
    }
}
