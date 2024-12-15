package faang.school.projectservice.service.campaign;

import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
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
