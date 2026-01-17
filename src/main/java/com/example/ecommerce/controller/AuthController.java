package com.example.ecommerce.controller;

import com.example.ecommerce.config.JwtProvider;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.EmailVerification;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.repository.EmailVerificationRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.CreateUserRequest;
import com.example.ecommerce.request.LoginRequest;
import com.example.ecommerce.request.VerifyCodeRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.service.AuthService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CustomeUserServiceImplementation;
import com.example.ecommerce.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomeUserServiceImplementation customeUserServiceImplementation;
    private final CartService cartService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> createUserHandler(
           @Valid @RequestBody CreateUserRequest request) throws UserException {

        log.info("Signup request received for email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email is already in use");
        }

        authService.sendVerificationCode(request);

        Map<String, String> response = new HashMap<>();
        response.put("email", request.getEmail());
        response.put("message", "Verification code sent successfully");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify-code")
    public ResponseEntity<AuthResponse> verify(@RequestBody VerifyCodeRequest request) {
        return ResponseEntity.ok(authService.verifyCodeAndCreateUser(request));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getEmail();
        log.info("Signin request received for email={}", username);

        Authentication authentication = authenticate(username, loginRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        log.info("JWT token generated successfully for email={}", username);

        AuthResponse authResponse = new AuthResponse(token, "Signin Success");

        log.info("Signin successful for email={}", username);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {

        log.info("Authenticating user with email={}", username);

        UserDetails userDetails =
                customeUserServiceImplementation.loadUserByUsername(username);

        if (userDetails == null) {
            log.error("Authentication failed: User not found for email={}", username);
            throw new BadCredentialsException("Invalid Username");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.error("Authentication failed: Invalid password for email={}", username);
            throw new BadCredentialsException("Invalid Password");
        }

        log.info("Authentication successful for email={}", username);

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }
}
