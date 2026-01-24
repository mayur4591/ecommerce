package com.example.ecommerce.kaffka.consumer;

import com.example.ecommerce.kaffka.events.OrderStatusUpdatedEvent;
import com.example.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusNotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "order-status-updated-topic",
            groupId = "order-notification-group"
    )
    public void consume(OrderStatusUpdatedEvent event) {

        log.info("Order status updated event received: {}", event);

        emailService.sendOrderStatusUpdateEmail(event);
    }
}

