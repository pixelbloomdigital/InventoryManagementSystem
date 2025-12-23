package com.pixelbloom.inventory.model;

import com.pixelbloom.inventory.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long productId;
    private Long categoryId;
    private Long subcategoryId;
    private Long inventoryId;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;  //RESERVED, RELEASED, CONFIRMED

    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
}
