package ru.corporationx.projectservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.corporationx.projectservice.model.dto.client.PaymentRequest;
import ru.corporationx.projectservice.model.dto.client.PaymentResponse;

@FeignClient(name = "payment-service", url = "${services.payment-service.host}:${services.payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sendPayment(@RequestBody PaymentRequest paymentRequest);
}
