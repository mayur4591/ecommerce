package com.example.ecommerce.service.Impl;

import com.example.ecommerce.config.JwtProvider;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.EmailVerification;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.EmailVerificationRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.CreateUserRequest;
import com.example.ecommerce.request.VerifyCodeRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final CartService cartService;



    @Override
    @Transactional
    public AuthResponse verifyCodeAndCreateUser(VerifyCodeRequest request) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No verification process found for this email"));

        if (emailVerification.getExpiryTime().isBefore(LocalDateTime.now())) {
            emailVerificationRepository.deleteByEmail(request.getEmail());
            throw new IllegalArgumentException("Verification code has expired");
        }

        if (!emailVerification.getVerificationCode().equals(request.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }


        emailVerificationRepository.deleteByEmail(request.getEmail());

        log.info("Creating new user with email={}", request.getEmail());

        User createdUser = new User();
        createdUser.setEmail(emailVerification.getEmail());
        createdUser.setPassword(emailVerification.getEncodedPassword());
        createdUser.setFirstName(emailVerification.getFirstName());
        createdUser.setLastName(emailVerification.getLastName());
        createdUser.setRole(emailVerification.getRole());

        User savedUser = userRepository.save(createdUser);
        log.info("User saved successfully with id={} and email={}", savedUser.getId(), savedUser.getEmail());

        Cart cart = cartService.createCart(savedUser);
        log.info("Cart created successfully for userId={}", savedUser.getId());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        log.info("JWT token generated successfully for email={}", request.getEmail());

        AuthResponse authResponse = new AuthResponse(token, "Signup Success");

        log.info("Signup completed successfully for email={}", request.getEmail());

        return authResponse;

    }

    @Override
    @Transactional
    public void sendVerificationCode(CreateUserRequest request) {
        String verificationCode = String.valueOf((int)(Math.random() * 9000) + 1000);

        EmailVerification emailVerification = new EmailVerification();
        emailVerification.setEmail(request.getEmail());
        emailVerification.setVerificationCode(verificationCode);

        emailVerification.setEncodedPassword(passwordEncoder.encode(request.getPassword()));
        emailVerification.setExpiryTime(LocalDateTime.now().plusMinutes(10));
        emailVerification.setFirstName(request.getFirstName());
        emailVerification.setLastName(request.getLastName());
        if (emailVerification.getRole() == null || emailVerification.getRole().isEmpty())
            emailVerification.setRole("ROLE_USER");

        emailVerificationRepository.save(emailVerification);

        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
    }
}
