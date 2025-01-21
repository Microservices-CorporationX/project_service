package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DonationDto(
        @NotNull @Positive BigDecimal amount,
        @NotNull @Positive Long campaignId,
        @NotNull Currency currency,
        @NotNull @Positive Long userId
) {
}
