/*
package com.pixelbloom.orders.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final KafkaOutboxPublisher publisher;

    @Scheduled(fixedDelayString = "${outbox.poll.delay-ms:5000}")
    public void publishOutboxEvents() {
        publisher.publishEvents();
    }

}*/
