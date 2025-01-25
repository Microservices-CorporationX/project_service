package faang.school.projectservice.service.payment;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponse makePayment(BigDecimal amount, Currency currency) {
        long paymentNumber = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, currency);

        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (paymentResponse.getStatusCode() != HttpStatus.OK) {
            throw new PaymentFailedException("Payment failed");
        }

        return paymentResponse.getBody();
    }
}
