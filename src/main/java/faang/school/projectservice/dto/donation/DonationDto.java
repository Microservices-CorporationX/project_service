package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DonationDto {
    @NotNull
    private Long paymentNumber;
    @NotNull
    @Min(value = 1)
    private BigDecimal amount;
    @NotNull
    private Long campaignId;
    @NotNull
    private Currency currency;
    @NotNull
    private Long userId;
}
