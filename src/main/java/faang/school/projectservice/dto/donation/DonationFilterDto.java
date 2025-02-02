package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationFilterDto {
    private BigDecimal minAmountPattern;
    private BigDecimal maxAmountPattern;
    private LocalDateTime donationTimePattern;
    private Currency currencyPattern;
}
