package com.example.ecommerce.kaffka.events;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderPlacedEvent implements Serializable {

    private Long orderId;
    private String email;
    private Double amount;
    private int itemCount;

}

