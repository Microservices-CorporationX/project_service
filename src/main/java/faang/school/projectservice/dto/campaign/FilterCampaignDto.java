package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;

import java.time.LocalDateTime;

public record FilterCampaignDto(
        LocalDateTime createdAt,
        CampaignStatus status,
        Long createdBy
) {
}
