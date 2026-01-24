package com.example.ecommerce.kaffka.consumer;

import com.example.ecommerce.kaffka.events.OrderPlacedEvent;
import com.example.ecommerce.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEmailConsumer {

    private final EmailService emailService;

    public OrderEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "order-placed-topic",
            groupId = "order-notification-group"
    )
    public void listen(OrderPlacedEvent event) {
        emailService.sendOrderConfirmation(event);
    }
}
