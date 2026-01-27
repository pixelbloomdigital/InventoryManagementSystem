package com.pixelbloom.orders.restClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "products-service", url = "http://localhost:9094")
public interface ProductsClient {

    @GetMapping("/api/products/reviews/customer/{customerId}")
    List<Object> getCustomerReviews(@PathVariable Long customerId);

    @GetMapping("/api/products/reviews/product/{productId}/customer/{customerId}")
    Object getProductReviewByCustomer(@PathVariable Long productId, @PathVariable Long customerId);

    @PostMapping("/api/products/reviews")
    Object submitReview(@RequestBody Map<String, Object> reviewRequest);
}
