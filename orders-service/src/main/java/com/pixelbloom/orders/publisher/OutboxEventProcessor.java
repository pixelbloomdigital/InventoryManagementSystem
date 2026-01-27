/*
package com.pixelbloom.orders.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixelbloom.orders.event.OrderCreatedEvent;
import com.pixelbloom.orders.event.OrderEmailEvent;
import com.pixelbloom.orders.model.Order;
import com.pixelbloom.orders.model.OrderItem;
import com.pixelbloom.orders.model.OrderOutbox;
import com.pixelbloom.orders.repository.OrderItemRepository;
import com.pixelbloom.orders.repository.OrderOutboxRepository;
import com.pixelbloom.orders.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

//@Component
@Slf4j
public class OutboxEventProcessor {

    @Autowired
    private OrderOutboxRepository outboxRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EmailEventPublisher emailEventPublisher;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, OrderEmailEvent> kafkaTemplate;

    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void processFailedEvents() {
        List<OrderOutbox> failedEvents = outboxRepository.findByStatusIn(Arrays.asList("NEW", "FAILED"));

        for (OrderOutbox event : failedEvents) {
            try {
                // Retry publishing the event
                republishEvent(event);

                // Mark as processed
                event.setStatus("PROCESSED");
                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);

                log.info("Successfully reprocessed outbox event: {}", event.getId());

            } catch (Exception e) {
                log.warn("Failed to reprocess outbox event {}: {}", event.getId(), e.getMessage());
                event.setStatus("FAILED");
                event.setRetryCount(event.getRetryCount() + 1);
                outboxRepository.save(event);
            }
        }
    }

    private void republishEvent(OrderOutbox event) {
        switch (event.getEventType()) {
            case "ORDER_CREATED":
                orderEventPublisher.publishOrderCreated(buildOrderCreatedEvent(event));
                break;
            case "ORDER_CONFIRMED":
                // Direct Kafka publish - bypass EmailEventPublisher
                try {
                    OrderEmailEvent emailEvent = objectMapper.readValue(event.getPayload(), OrderEmailEvent.class);
                    kafkaTemplate.send("order-email-events", emailEvent.getOrderNumber(), emailEvent).get();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to republish email event", e);
                }
                break;
        }
    }


    //These methods reconstruct the original events from the stored JSON payload so they can be republished to Kafka when it's available again.

    private OrderEmailEvent buildEmailEvent(OrderOutbox event) {
        try {
            // Deserialize JSON payload back to Order object
            Order order = objectMapper.readValue(event.getPayload(), Order.class);

            return OrderEmailEvent.builder()
                    .eventType("ORDER_CONFIRMED")
                    .orderNumber(order.getOrderNumber())
                    .customerId(order.getCustomerId())
                    .eventTime(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build email event from outbox", e);
        }
    }
    private OrderCreatedEvent buildOrderCreatedEvent(OrderOutbox event) {
        try {
            // Deserialize JSON payload back to Order object
            Order order = objectMapper.readValue(event.getPayload(), Order.class);

            // Get order items from DB
            List<OrderItem> items = orderItemRepository.findByOrderNumber(order.getOrderNumber());

            return OrderCreatedEvent.builder()
                    .eventType("ORDER_CREATED")
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .customerId(order.getCustomerId())
                    .totalAmount(order.getTotalAmount())
                    .currency(order.getCurrency())
                    .paymentMode(order.getPaymentMode())
                    .createdAt(order.getCreatedAt())
                    .items(items.stream()
                            .map(item -> OrderCreatedEvent.OrderItemDetail.builder()
                                    .productId(item.getProductId())
                                    .barcode(item.getBarcode())
                                    .quantity(item.getQuantity())
                                    .price(item.getTotalPrice())
                                    .build())
                            .toList())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build order created event from outbox", e);
        }
    }
}
*/
