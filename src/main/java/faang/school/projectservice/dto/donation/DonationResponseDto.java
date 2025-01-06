package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.Campaign;

import java.math.BigDecimal;

public record DonationResponseDto(
        Long id,
        BigDecimal amount,
        Long campaignId,
        Long paymentNumber,
        Currency currency,
        Long userId
) {
}
