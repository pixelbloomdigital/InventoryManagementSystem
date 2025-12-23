package com.pixelbloom.inventory.repository;

import com.pixelbloom.inventory.enums.TransactionType;
import com.pixelbloom.inventory.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryTransactionRepository  extends JpaRepository<InventoryTransaction, Long> {
    // ===================== DAILY SALES ANALYTICS =====================

    @Query(
            value = """
    SELECT 
        DATE(transaction_date),
        SUM(sell_price * quantity),
        SUM(buy_price * quantity),
        SUM(profit)
    FROM inventory_transaction
    WHERE transaction_type = :type
    GROUP BY DATE(transaction_date)
    ORDER BY DATE(transaction_date)
    """,
            nativeQuery = true
    )
    List<Object[]> getDailySales(@Param("type") String type);
    @Query(
            value = """
    SELECT 
        DATE(transaction_date)            AS txnDate,
        SUM(sell_price * quantity)        AS totalSales,
        SUM(buy_price * quantity)         AS totalCost,
        SUM(profit)                       AS totalProfit
    FROM inventory_transaction
    WHERE transaction_type = :type
      AND transaction_date >= :fromDate
      AND transaction_date < DATE_ADD(:toDate, INTERVAL 1 DAY)
    GROUP BY DATE(transaction_date)
    ORDER BY DATE(transaction_date)
    """,
            nativeQuery = true
    )
    List<Object[]> getSalesByDate(@Param("type")String type, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}

