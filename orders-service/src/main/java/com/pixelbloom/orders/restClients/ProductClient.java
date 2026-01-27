package com.pixelbloom.orders.restClients;

import com.pixelbloom.orders.responseEntity.ProductRefundEligibilityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {
    private final RestTemplate restTemplate;
    private static final String PRODUCT_REFUND_ELIGIBILITY_URL =
            "http://localhost:9094/api/products/{productId}/refund-eligibility?categoryId={categoryId}&subcategoryId={subcategoryId}";

    public boolean isProductRefundEligible(Long productId, Long categoryId, Long subcategoryId) {
        try {
            ResponseEntity<ProductRefundEligibilityResponse> response =
                    restTemplate.getForEntity(PRODUCT_REFUND_ELIGIBILITY_URL,
                            ProductRefundEligibilityResponse.class,
                            productId, categoryId, subcategoryId);

            return response.getBody() != null && response.getBody().isRefundEligible();
        } catch (Exception e) {
            log.error("Failed to check refund eligibility for productId: {}", productId, e);
            return false; // Default to not eligible if service is down
        }
    }
}