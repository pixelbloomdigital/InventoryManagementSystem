/*
package com.pixelbloom.orders.publisher;

import com.pixelbloom.orders.model.OrderOutbox;
import com.pixelbloom.orders.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
//this was also another outbox processor
@Component
@RequiredArgsConstructor
public class OrderOutboxPublisher {

    private final OrderOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OrderOutbox> events =
                outboxRepository.findTop50ByStatusOrderByCreatedAtAsc("NEW", PageRequest.of(0, 50));

        for (OrderOutbox event : events) {
            try {
                kafkaTemplate.send("order-events", event.getPayload());

                event.setStatus("SENT");

                outboxRepository.save(event);

            } catch (Exception ex) {
                event.setStatus("FAILED");
                System.out.println("OrderOutboxPublisher.publishEvents===>>>>>>>>>>>>>>>"+ex.getMessage());
                event.setFailureReason(ex.getMessage());
                outboxRepository.save(event);
            }
        }
    }
}
*/
