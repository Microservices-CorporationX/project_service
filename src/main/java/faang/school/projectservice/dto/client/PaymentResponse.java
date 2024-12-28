package faang.school.projectservice.dto.client;

import java.math.BigDecimal;

public record PaymentResponse(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
