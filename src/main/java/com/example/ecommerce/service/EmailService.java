package com.example.ecommerce.service;

import com.example.ecommerce.kaffka.events.OrderPlacedEvent;
import com.example.ecommerce.kaffka.events.OrderStatusUpdatedEvent;
import org.springframework.stereotype.Service;


public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode);
    public void sendOrderConfirmation(OrderPlacedEvent event);
    public void sendOrderStatusUpdateEmail(OrderStatusUpdatedEvent event);
}
