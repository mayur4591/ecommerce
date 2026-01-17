package com.example.ecommerce.service;

import com.example.ecommerce.request.CreateUserRequest;
import com.example.ecommerce.request.VerifyCodeRequest;
import com.example.ecommerce.response.AuthResponse;

public interface AuthService {
    AuthResponse verifyCodeAndCreateUser(VerifyCodeRequest verifyCodeRequest);
    void sendVerificationCode(CreateUserRequest createUserRequest);
}
