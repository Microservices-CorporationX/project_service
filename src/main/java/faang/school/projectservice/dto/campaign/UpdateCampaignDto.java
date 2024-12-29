package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateCampaignDto (
        String title,
        String description,
        BigDecimal goal,
        CampaignStatus status,
        Currency currency,
        @NotNull @Positive Long updatedBy
) {
}
