package com.pixelbloom.inventory.serviceImpl;

import com.pixelbloom.inventory.enums.*;
import com.pixelbloom.inventory.exception.InsufficientInventoryException;
import com.pixelbloom.inventory.exception.ResourceNotFoundException;
import com.pixelbloom.inventory.model.*;
import com.pixelbloom.inventory.repository.InventoryReservationRepository;
import com.pixelbloom.inventory.repository.InventoryTransactionRepository;
import com.pixelbloom.inventory.requestEntity.AdminInventoryUpdateRequest;
import com.pixelbloom.inventory.requestEntity.PriceUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pixelbloom.inventory.repository.InventoryRepository;
import com.pixelbloom.inventory.service.InventoryService;
import java.sql.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryReservationRepository reservationRepository,
                                InventoryTransactionRepository transactionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.transactionRepository = transactionRepository;
    }

    // ===================== ADMIN APIs =====================
    @Override
    @Transactional
    public Inventory addInventory(Inventory inventory) {
        inventory.setInventoryStatus(InventoryStatus.AVAILABLE);
        inventory.setPlatformStatus(PlatformStatus.ENABLED);
        inventory.setConditionStatus(ConditionStatus.GOOD);
        inventory.setIsCustomerReturned(false);
        inventory.setIsWarehouseDamaged(false);
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory updatePrice(String barcode, PriceUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

       // if (request.getSellingPrice().compareTo(request.getMrp()) > 0)
        //    throw new IllegalArgumentException("Selling price cannot exceed MRP");

     //   if (request.getSellingPrice().compareTo(request.getShowroomPrice()) < 0)
       //     throw new IllegalArgumentException("Selling price cannot be less than showroom price");

        inventory.setMrp(request.getMrp());
        inventory.setShowroomPrice(request.getShowroomPrice());
        inventory.setSellingPrice(request.getSellingPrice());
        inventory.setUpdatedBy(request.getUpdatedBy());
     //   inventory.setTransactionDate(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void disableInventory(String barcode, Long adminId) {
        Inventory inventory = inventoryRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
        if (inventory.getInventoryStatus() == InventoryStatus.SALE)
            throw new IllegalStateException("Cannot disable sold inventory");

        inventory.setPlatformStatus(PlatformStatus.DISABLED);
        inventory.setInventoryStatus(InventoryStatus.REMOVED);
        inventory.setUpdatedBy(adminId);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void enableInventory(String barcode, Long adminId) {
        Inventory inventory = inventoryRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.setPlatformStatus(PlatformStatus.ENABLED);
        inventory.setInventoryStatus(InventoryStatus.AVAILABLE);
        inventory.setUpdatedBy(adminId);

        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public Inventory updateInventoryStatus(String barcode, AdminInventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (request.getInventoryStatus() != null)
            inventory.setInventoryStatus(InventoryStatus.from(request.getInventoryStatus()));

        if (request.getPlatformStatus() != null)
            inventory.setPlatformStatus(PlatformStatus.from(request.getPlatformStatus()));

        if (request.getConditionStatus() != null)
            inventory.setConditionStatus(ConditionStatus.from(request.getConditionStatus()));

        if (request.getIsCustomerReturned() != null)
            inventory.setIsCustomerReturned(request.getIsCustomerReturned());

        if (request.getIsWarehouseDamaged() != null)
            inventory.setIsWarehouseDamaged(request.getIsWarehouseDamaged());

        inventory.setUpdatedBy(request.getUpdatedBy());
        return inventoryRepository.save(inventory);
    }

    // ===================== ORDER ORCHESTRATION =====================
  /*  @Override
    @Transactional
    public void reserveInventoryForOrder(InventoryReserveRequest request) {

        for (ReserveItem item : request.getItems()) {

            // Step 1: Fetch ONLY required units
            List<Inventory> inventories =
                    inventoryRepository.findTopNAvailable(
                            item.getProductId(),
                            item.getCategoryId(),
                            item.getSubcategoryId(),
                            InventoryStatus.AVAILABLE,
                            PlatformStatus.ENABLED,
                            ConditionStatus.GOOD,
                            item.getQuantity()
                    );

            if (inventories.size() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient stock for productId=" + item.getProductId()
                );
            }

            // Step 2: Reserve EACH unit
            for (Inventory inv : inventories) {


                inv.setInventoryStatus(InventoryStatus.RESERVED);
                inventoryRepository.save(inv);

                InventoryReservation reservation =
                        InventoryReservation.builder()
                                .orderId(request.getOrderId())
                                .inventoryId(inv.getId()) // 🔥 KEY
                                .productId(inv.getProductId())
                                .categoryId(inv.getCategoryId())
                                .subcategoryId(inv.getSubcategoryId())
                                .quantity(1)
                                .reservationStatus(ReservationStatus.RESERVED)
                                .reservedAt(LocalDateTime.now())
                                .expiresAt(LocalDateTime.now().plusMinutes(15))
                                .build();

                reservationRepository.save(reservation);
            }
        }
    }*/
    @Override
    @Transactional
    public void releaseInventoryForOrder(Long orderId) {

        List<InventoryReservation> reservations =
                reservationRepository.findByOrderId(orderId);

        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "INTERNAL_SERVER_ERROR  No reservations found for orderId=" + orderId);
             // idempotent release
        }
        /* What is happening internally findByOrderId(5008) → returns empty list
if (reservations.isEmpty()) { return; }Method exits normally Controller returns 200 OK
So nothing is deleted, nothing is updated — but no error is thrown, hence 200 OK.
This is called idempotent API behavior.*/

        for (InventoryReservation reservation : reservations) {

            Inventory inventory = inventoryRepository.findById(
                    reservation.getInventoryId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "NOT_FOUND Inventory not found for id=" + reservation.getInventoryId()
                    )
            );

            // Restore inventory
            inventory.setInventoryStatus(InventoryStatus.AVAILABLE);
            inventoryRepository.save(inventory);
        }

        // HARD DELETE reservations
        reservationRepository.deleteAll(reservations);
    }

 /*  @Override
    @Transactional
    public void confirmSale(InventoryConfirmSaleRequest request) {
        for (var item : request.getItems()) {
            List<Long> inventoryIds = inventoryRepository.findSellableInventoryIds(
                    item.getProductId(),
                    item.getCategoryId(),
                    item.getSubcategoryId(),
                    InventoryStatus.AVAILABLE,
                    PlatformStatus.ENABLED,
                    PageRequest.of(0, item.getQuantity())
            );

            if (inventoryIds.size() < item.getQuantity())
                throw new InsufficientInventoryException( "Insufficient stock for productId=" +  item.getProductId() +
                            ", requested=" + item.getQuantity() +
                            ", available=" + inventoryIds.size()
            );

            inventoryRepository.updateInventoryStatus(inventoryIds, InventoryStatus.SOLD);

            transactionRepository.saveAll(
                    inventoryIds.stream()
                            .map(id -> {
                                return InventoryTransaction.sale(
                                        id,
                                        request.getOrderId(),
                                        item.getProductId(),
                                        item.getCategoryId(),
                                        item.getSubcategoryId()
                                );
                            })
                            .toList()
            );
        }
    }*/


    @Transactional
    public void confirmOrder(Long orderId) {

        List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);
        for (InventoryReservation r : reservations) {
            Inventory inventory = inventoryRepository.findById(r.getInventoryId()).orElseThrow();

            // 1️⃣ Update inventory state
            inventory.setInventoryStatus(InventoryStatus.SALE);
            inventoryRepository.save(inventory);

            // 2️⃣ Insert SALE transaction
            InventoryTransaction tx = InventoryTransaction.sale(inventory,r.getOrderId(),r.getQuantity());

            transactionRepository.save(tx);

            // 3️⃣ Mark reservation confirmed
            r.setReservationStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(r);
        }
    }




    @Override
    @Transactional
    public void reserveInventoryForOrder(InventoryReserveRequest request) {

        for (ReserveItem item : request.getItems()) {

            List<Inventory> availableUnits =
                    inventoryRepository.findTopNAvailable(
                            item.getProductId(),
                            item.getCategoryId(),
                            item.getSubcategoryId(),
                            InventoryStatus.AVAILABLE,
                            PlatformStatus.ENABLED,
                            ConditionStatus.GOOD,
                            item.getQuantity()
                    );

            if (availableUnits.size() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        "Insufficient stock for productId=" + item.getProductId()
                );
            }

            availableUnits.stream()
                    .limit(item.getQuantity())
                    .forEach(inv -> {

                        // UPDATE inventory (not delete)
                        inv.setInventoryStatus(InventoryStatus.RESERVED);
                        inventoryRepository.save(inv);

                        // INSERT reservation
                        reservationRepository.save(
                                InventoryReservation.builder()
                                        .orderId(request.getOrderId())
                                        .inventoryId(inv.getId())
                                        .productId(inv.getProductId())
                                        .categoryId(inv.getCategoryId())
                                        .subcategoryId(inv.getSubcategoryId())
                                        .quantity(1)
                                        .reservationStatus(ReservationStatus.RESERVED)
                                        .reservedAt(LocalDateTime.now())
                                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                                        .build());

                     });
        }
    } //working but have above dup working too


    // ===================== SELL CHECK =====================
    @Override
    public SellCheckResponse checkSellable(SellCheckRequest request) {
        List<SellItemResult> results = request.getItems().stream().map(item -> {
            int sellableCount = inventoryRepository.countSellableUnits(
                    item.getProductId(),
                    item.getCategoryId(),
                    item.getSubcategoryId(),
                    InventoryStatus.AVAILABLE,
                    PlatformStatus.ENABLED,
                    ConditionStatus.GOOD
            );
            boolean sellable = sellableCount >= item.getRequestedQuantity();
            if (!sellable) {
                throw new InsufficientInventoryException(
                        "Insufficient stock for productId=" + item.getProductId()
                );
            }

            return new SellItemResult(item.getProductId(), item.getCategoryId(), item.getSubcategoryId(),
                    sellableCount, sellable);
        }).toList();

        return new SellCheckResponse(results);
    }

    // ===================== STOCK QUERIES =====================
    @Override
    public long countAvailableByProduct(Long productId) {
        return inventoryRepository.countByProductIdAndInventoryStatusAndPlatformStatus(
                productId, InventoryStatus.AVAILABLE, PlatformStatus.ENABLED);
    }

    @Override
    public long countAvailableByCategory(Long categoryId) {
        return inventoryRepository.countByCategoryIdAndInventoryStatusAndPlatformStatus(
                categoryId, InventoryStatus.AVAILABLE, PlatformStatus.ENABLED);
    }

    @Override
    public long countAvailableBySubCategory(Long subcategoryId) {
        return inventoryRepository.countBysubcategoryIdAndInventoryStatusAndPlatformStatus(
                subcategoryId, InventoryStatus.AVAILABLE, PlatformStatus.ENABLED);
    }

    @Override
    public List<AvailableStockResponse> getAvailableStockList(Long productId, Long categoryId, Long subcategoryId) {

        List<Inventory> inventories =
                inventoryRepository.findAvailableStock(
                        productId,
                        categoryId,
                        subcategoryId,
                        InventoryStatus.AVAILABLE,
                        PlatformStatus.ENABLED
                );

        return inventories.stream()
                .map(i -> new AvailableStockResponse(
                        i.getId(),
                        i.getBarcode(),
                        i.getProductId(),
                        i.getCategoryId(),
                        i.getSubcategoryId(),
                        i.getInventoryStatus(),
                        i.getPlatformStatus(),
                        i.getConditionStatus()
                ))
                .toList();

    }

    // ===================== ANALYTICS =====================
    @Override
    public List<DailySalesResponse> getDailySales(TransactionType type) {

        List<Object[]>  rows = transactionRepository.getDailySales(type.name());
        System.out.println("Rows size = " + rows.size());


        rows.forEach(r -> {
            System.out.println(
                    "date=" + r[0] +
                            ", sales=" + r[1] +
                            ", cost=" + r[2] +
                            ", profit=" + r[3]
            );
        });
        return rows.stream()
                .map(r -> new DailySalesResponse(
                        ((Date) r[0]).toLocalDate(),
                        (BigDecimal) r[1],
                        (BigDecimal) r[2],
                        (BigDecimal) r[3]
                ))
                .toList();
    }



    @Override
    public List<DailySalesResponse> getSales(TransactionType type, LocalDate fromDate, LocalDate toDate) {

        List<Object[]> rows = transactionRepository.getSalesByDate(type.name(), fromDate, toDate);
        rows.forEach(r -> {
            System.out.println("Row: Date: " + r[0] + ",Sales " + r[1] + ",Cost " + r[2] + ",Profit " + r[3]);
        });
        return rows.stream()
                .map(r -> new DailySalesResponse(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        (BigDecimal) r[1],
                        (BigDecimal) r[2],
                        (BigDecimal) r[3]
                ))
                .toList();
    }

}


