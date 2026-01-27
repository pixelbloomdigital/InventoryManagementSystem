package com.pixelbloom.orders.restClients;

import com.pixelbloom.orders.requestEntity.PaymentRequest;
import com.pixelbloom.orders.requestEntity.RefundPaymentRequest;
import com.pixelbloom.orders.responseEntity.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

   // @Value("${payment.service.url}")
    private String paymentServiceUrl="http://localhost:9096";

    public PaymentResponse pay(PaymentRequest request) {
        return restTemplate.postForObject(paymentServiceUrl + "/api/payments/pay",request,PaymentResponse.class);
        }

    public PaymentResponse refund(RefundPaymentRequest refundPaymentRequest) {
        return restTemplate.postForObject(paymentServiceUrl + "/api/payments/refund", refundPaymentRequest, PaymentResponse.class);
    }
}
