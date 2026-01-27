package com.pixelbloom.orders.restClients;

import com.pixelbloom.orders.config.FeignConfig;
import com.pixelbloom.orders.requestEntity.InventoryReleaseRequest;
import com.pixelbloom.orders.requestEntity.InventoryReserveRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "api-gateway",url = "${api.gateway.url:http://localhost:8080}",
        configuration = FeignConfig.class)
public interface InventoryClientF {

    @PostMapping("/api/inventory/reserve")
    void reserveInventory(@RequestBody InventoryReserveRequest request);

    @PostMapping("/api/inventory/confirm")
    void confirmSale(@RequestParam("orderNumber") String orderNumber);

    @PostMapping("/api/inventory/release")
    void releaseReservation(@RequestBody InventoryReleaseRequest request);
}
