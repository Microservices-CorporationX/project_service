package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FilterDonationDto(
        LocalDateTime donationTime,
        Currency currency,
        BigDecimal minDonationAmount,
        BigDecimal maxDonationAmount
) {
}
