package com.pixelbloom.inventory.model;

import lombok.Data;

import java.util.List;

@Data
public class InventoryReserveRequest {
    private Long orderId;
    private List<ReserveItem> items;
}