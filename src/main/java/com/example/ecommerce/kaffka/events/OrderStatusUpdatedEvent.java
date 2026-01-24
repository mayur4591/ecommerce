package com.example.ecommerce.kaffka.events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdatedEvent {

    private Long orderId;
    private String email;
    private String newStatus;
}

