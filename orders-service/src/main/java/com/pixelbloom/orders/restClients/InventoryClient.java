package com.pixelbloom.orders.restClients;

import com.pixelbloom.orders.enums.OrderStatus;
import com.pixelbloom.orders.exception.InspectionException;
import com.pixelbloom.orders.exception.InventoryReservationException;
import com.pixelbloom.orders.requestEntity.*;
import com.pixelbloom.orders.responseEntity.InventoryInspectionResponse;
import com.pixelbloom.orders.responseEntity.OrderPhysicalInspectionResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventoryClient {
    private final RestTemplate restTemplate;
    private static final String INVENTORY_URL = "http://localhost:9093/api/inventory/reservations";
    private static final String INVENTORY_CONFIRM_URL = "http://localhost:9093/api/inventory/sales/confirm?orderNumber=";
    private static final String INVENTORY_RELEASE_URL = "http://localhost:9093/api/inventory/reservations/release?orderNumber=";
    private static final String INVENTORY_PHYSICAL_STATUS_URL = "http://localhost:9093/api/inventory/inspectionStatus";
   private  static final String INITIATE_INVENTORY_STATUS_URL = "http://localhost:9093/api/inventory/orderReturn-Initiated";
    private static final String INVENTORY_PHYSICAL_STATUS_GET_DONE = "http://localhost:9093/api/inventory/inspection";


    public void reserveInventory(InventoryReserveRequest request,String authHeader) {
        System.out.println("InventoryClient.reserveInventory===>>>>>>>>>>>>>>>"+request);
        try {
            ResponseEntity<ReserveItemResponse> response = restTemplate.postForEntity(INVENTORY_URL, request, ReserveItemResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new InventoryReservationException("Inventory reservation failed");
            }
        } catch (HttpClientErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            try {
                com.fasterxml.jackson.databind.JsonNode errorJson = new com.fasterxml.jackson.databind.ObjectMapper().readTree(errorBody);
                String message = errorJson.get("message").asText();
                throw new InventoryReservationException(message);
            } catch (Exception e) {
                throw new InventoryReservationException(errorBody);
            }
        }
    }



    public void confirmSale(String orderNumber) {
        System.out.println("InventoryClient.confirmSale===>>>>>>>>>>>>>>>"+orderNumber);
       ResponseEntity<Void> response = restTemplate.postForEntity(INVENTORY_CONFIRM_URL + orderNumber,null, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InventoryReservationException("Inventory sale confirmation failed");
        }
    }


    public void releaseReservation(InventoryReleaseRequest request) {
        System.out.println("InventoryClient.releaseReservation===>>>>>>>>>>>>>>>"+request.getOrderNumber());
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(INVENTORY_RELEASE_URL, request, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new InventoryReservationException("Inventory reservation release failed");
            }
        } catch (Exception e) {
            System.err.println("Failed to release inventory: " + e.getMessage());
            // throw new InventoryReservationException("Inventory reservation release failed: " + e.getMessage());
        }
    }


    public void restock(String orderNumber) {

    }

    public InventoryInspectionResponse getPhysicalStatusApproval(
            OrderInspectionRequest request) {

        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(INVENTORY_PHYSICAL_STATUS_URL)
                    .queryParam("orderNumber", request.getOrderNumber())
                    .queryParam("barcode", request.getBarcode());

            ResponseEntity<InventoryInspectionResponse> response =
                    restTemplate.getForEntity(
                            builder.toUriString(),
                            InventoryInspectionResponse.class
                    );

            return response.getBody();

        } catch (HttpServerErrorException e) {
            throw new InspectionException(
                    "Inventory service unavailable");
        } catch (HttpClientErrorException e) {
            throw new InspectionException(
                    "Invalid inspection request");
        }
    }
    public OrderPhysicalInspectionResponse getPhysicalVerificationDone(OrderPhysicalVerificationRequest request) {
        System.out.println("InventoryClient.getPhysicalVerificationDone ===>>>>>>>>>>>>>>>" + request.getOrderNumber());

        try {
            InventoryInspectionRequest inventoryRequest = InventoryInspectionRequest.builder()
                    .orderNumber(request.getOrderNumber())
                    .barcode(request.getBarcode())
                    .approved(request.getApproved()) // Pass approved status
                    .inspectedBy(request.getInspectedBy())
                    .inspectorRemarks(request.getInspectorRemarks())
                    .rejectionReason(request.getRejectionReason())
                     .build();

            ResponseEntity<OrderPhysicalInspectionResponse> response = restTemplate.postForEntity(
                    INVENTORY_PHYSICAL_STATUS_GET_DONE,
                    inventoryRequest,
                    OrderPhysicalInspectionResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new InspectionException("Physical verification failed");
            }
        } catch (Exception e) {
            System.err.println("Failed to get physical verification: " + e.getMessage());
            throw new InspectionException("Physical verification service unavailable: " + e.getMessage());
        }
    }


    public void returnInitiated(InventoryInitiateReturnRequest request) {
        System.out.println("InventoryClient.returnInitiated===>>>>>>>>>>>>>>>" + request.getOrderNumber());

        ResponseEntity<Void> response = restTemplate.postForEntity(
                INITIATE_INVENTORY_STATUS_URL,
                request,
                Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InventoryReservationException("Inventory return initiation failed");
        }
    }

}



