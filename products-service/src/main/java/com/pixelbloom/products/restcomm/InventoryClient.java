package com.pixelbloom.products.restcomm;

import com.pixelbloom.products.model.SellCheckRequest;
import com.pixelbloom.products.model.SellCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestTemplate restTemplate;

    private final String INVENTORY_URL = "http://localhost:9093/api/inventory/sell-check";

    public SellCheckResponse checkSellable(SellCheckRequest request) {
        SellCheckResponse response = restTemplate.postForObject(INVENTORY_URL, request, SellCheckResponse.class);
        System.out.println("Inventory Response: " + response);

        return response;
    }


   /* public SellCheckResponse checkSellable(SellCheckRequest request) {
        // HARDCODED DUMMY RESPONSE matching actual inventory format
        List<SellCheckResponse.SellItemResult> results = List.of(
                new SellCheckResponse.SellItemResult(1L, 110L, 10L, 2, true,""),
                new SellCheckResponse.SellItemResult(2L, 110L, 10L, 1, true,""),
                new SellCheckResponse.SellItemResult(3L, 10L, 101L, 3, false,""),
                new SellCheckResponse.SellItemResult(4L, 10L, 101L, 1, true,""),
                new SellCheckResponse.SellItemResult(101L, 11L, 1L, 1, false,""),
                new SellCheckResponse.SellItemResult(102L, 11L, 1L, 2, true,""),
                new SellCheckResponse.SellItemResult(103L, 12L, 2L, 1, true,"")
        );

        return new SellCheckResponse(results);
    }*/

}
