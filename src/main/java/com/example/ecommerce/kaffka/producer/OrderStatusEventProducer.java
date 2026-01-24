package com.example.ecommerce.kaffka.producer;

import com.example.ecommerce.kaffka.events.OrderStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusEventProducer {

    private static final String TOPIC = "order-status-updated-topic";

    private final KafkaTemplate<String, OrderStatusUpdatedEvent> kafkaTemplate;

    public void send(OrderStatusUpdatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}

