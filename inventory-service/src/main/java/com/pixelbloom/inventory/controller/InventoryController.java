package com.pixelbloom.inventory.controller;

import com.pixelbloom.inventory.enums.InventoryStatus;
import com.pixelbloom.inventory.enums.PlatformStatus;
import com.pixelbloom.inventory.enums.TransactionType;
import com.pixelbloom.inventory.model.*;
import com.pixelbloom.inventory.requestEntity.AdminInventoryUpdateRequest;
import com.pixelbloom.inventory.requestEntity.PriceUpdateRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pixelbloom.inventory.service.InventoryService;

import java.time.LocalDate;
import java.util.List;
/**
 * InventoryController
 *
 * Responsibility:
 * - Manage physical inventory (stock, condition, platform availability)
 * - Coordinate with Order-Service for reservation, release, sale confirmation
 * - Provide analytics on inventory movement
 *
 * IMPORTANT:
 * - Customers NEVER call inventory-service directly
 * - Order-service is the main orchestrator
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ======================================================
    // ===================== ADMIN APIs =====================
    // ======================================================

    /**
     * Admin adds a new physical inventory item (barcode-level)
     * Example: New TV unit received in warehouse
     */
    @PostMapping("/add")
    public Inventory addInventory(@RequestBody Inventory inventory) {
        return inventoryService.addInventory(inventory);
    }

    /**
     * Admin corrects pricing for a specific barcode
     * (MRP / showroom / selling price)
     */
    @PatchMapping("/{barcode}/price")
    public Inventory updatePrice(@PathVariable String barcode,@RequestBody PriceUpdateRequest request
    ) {
        return inventoryService.updatePrice(barcode, request);
    }

    /**
     * Admin disables an inventory item from platform visibility
     * Example: damaged / internal hold / audit
     */
    @PatchMapping("/{barcode}/disable")
    public void disableInventory(
            @PathVariable String barcode,
            @RequestParam Long adminId
    ) {
        inventoryService.disableInventory(barcode, adminId);
    }

    /**
     * Admin re-enables a previously disabled inventory item
     */
    @PatchMapping("/{barcode}/enable")
    public void enableInventory(
            @PathVariable String barcode,
            @RequestParam Long adminId
    ) {
        inventoryService.enableInventory(barcode, adminId);
    }

    /**
     * Admin updates inventory state (condition, platform status, flags)
     * Used for audits, QC checks, warehouse corrections
     */
    @PatchMapping("/{barcode}/status")
    public Inventory updateInventoryStatus(
            @PathVariable String barcode,
            @RequestBody AdminInventoryUpdateRequest request
    ) {
        return inventoryService.updateInventoryStatus(barcode, request);
    }

    // ======================================================
    // ============== ORDER–INVENTORY ORCHESTRATION =========
    // ======================================================

    /**
     * Reserve inventory AFTER payment success
     * Called ONLY by order-service
     *
     * Inventory moves:
     * AVAILABLE → RESERVED
     */
    @PostMapping("/reservations")
    public void reserveInventory(@RequestBody InventoryReserveRequest request) {
        inventoryService.reserveInventoryForOrder(request);
    }

    /**
     * Release reserved inventory
     * Scenarios:
     * - Payment failed
     * - Order cancelled
     * - Refund approved
     *
     * Inventory moves:
     * RESERVED → AVAILABLE
     */
    @PostMapping("/reservations/release")
    public void releaseInventory(@RequestParam Long orderId) {
        inventoryService.releaseInventoryForOrder(orderId);
    }

    /**
     * Confirm sale after delivery success
     * Called by order-service after shipping confirmation
     *
     * Inventory moves: RESERVED → SOLD  Inventory transaction is recorded
     * Order Paid (Orders Service)  When payment is successful: ORDER_STATUS = CONFIRMED PAYMENT_STATUS = SUCCESS
     */
    @PostMapping("/sales/confirm")
    public void confirmSale(@RequestParam Long orderId) {
       inventoryService.confirmOrder(orderId);
    }

    // ======================================================
    // ===================== SELL CHECK =====================
    // ======================================================

    /**
     * Sell-check API
     *
     * Used by product-service / order-service
     * Determines if requested quantity is sellable=false ->means stock quanity is not available or status condition of product is not..     //true -> can sell and customer can add to cart this api will check availability while adding to cart
     * DOES NOT reserve stock
     */

    @PostMapping("/sell-check")
    public SellCheckResponse checkSellable(@RequestBody SellCheckRequest request) {
        return inventoryService.checkSellable(request);
    }

    // ======================================================
    // ===================== STOCK QUERIES ==================
    // ======================================================

    /**
     * Get available stock count by product
     */
    @GetMapping("/stock/product-count")
    public long stockByProduct(@RequestParam  Long productId) {
        return inventoryService.countAvailableByProduct(productId);
    }

    /**
     * Get available stock count by category
     */
    @GetMapping("/stock/category-count")
    public long stockByCategory(@RequestParam  Long categoryId) {
        return inventoryService.countAvailableByCategory(categoryId);
    }

    /**
     * Get available stock count by subcategory
     */
    @GetMapping("/stock/subcategory-count")
    public long stockBySubCategory(@RequestParam Long subcategoryId) {
        return inventoryService.countAvailableBySubCategory(subcategoryId);
    }

    /*
    optional GET /api/inventory/stock/product?productId=101
GET /api/inventory/stock/product?categoryId=11
GET /api/inventory/stock/product?subcategoryId=1
     */
    @GetMapping("/stock/product")
    public List<AvailableStockResponse> getAvailableStockList(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subcategoryId
    ) {
        return inventoryService.getAvailableStockList(productId, categoryId, subcategoryId);
    }

    // ======================================================
    // ===================== ANALYTICS ======================
    // ======================================================

    /**
     * Daily sales analytics Used by admin dashboards */
   @GetMapping("/analytics/sales/daily")
   public List<DailySalesResponse> getDailySales(@RequestParam ("type") TransactionType type) {
       var result = inventoryService.getDailySales(type);
       System.out.println("Controller size = " + result.size());
       return inventoryService.getDailySales(type);
   }

   @GetMapping("/analytics/sales/from-to")
    public List<DailySalesResponse> getSalesByDate
           (@RequestParam ("type") TransactionType type,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate toDate){
       var response = inventoryService.getSales(type,fromDate,toDate);
   System.out.println("Sales from date to Date"+response);
       return inventoryService.getSales(type,fromDate,toDate);

   }
}