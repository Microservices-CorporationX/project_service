package faang.school.projectservice.dto.donation;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto {
    private Long id;
    private Long userId;
    private LocalDateTime donationTime;

    @NotNull
    @Positive
    private Long paymentNumber;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    @Positive
    private Long campaignId;
    @NotNull
    private Currency currency;
}