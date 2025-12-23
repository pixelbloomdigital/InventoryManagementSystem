package com.pixelbloom.inventory.repository;

import com.pixelbloom.inventory.enums.ReservationStatus;
import com.pixelbloom.inventory.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface InventoryReservationRepository  extends JpaRepository<InventoryReservation, Long> {


    List<InventoryReservation> findByOrderIdAndReservationStatus(
            Long orderId,
            ReservationStatus reservationStatus
    );

    List<InventoryReservation> findByOrderId(Long orderId);
}
