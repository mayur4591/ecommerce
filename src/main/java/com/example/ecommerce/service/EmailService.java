package com.example.ecommerce.service;

import org.springframework.stereotype.Service;


public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode);
}
