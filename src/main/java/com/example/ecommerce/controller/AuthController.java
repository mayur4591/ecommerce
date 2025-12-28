package com.example.ecommerce.controller;

import com.example.ecommerce.config.JwtProvider;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.LoginRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CustomeUserServiceImplementation;
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

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomeUserServiceImplementation customeUserServiceImplementation;

    @Autowired
    private CartService cartService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {

        log.info("Signup request received for email={}", user.getEmail());

        String email = user.getEmail();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRole();

        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {
            log.error("Signup failed: Email {} already exists", email);
            throw new UserException("Email is allrady used with another account");
        }

        log.info("Creating new user with email={}", email);

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setFirstName(firstName);
        createdUser.setLastName(lastName);
        createdUser.setRole(role);

        User savedUser = userRepository.save(createdUser);
        log.info("User saved successfully with id={} and email={}", savedUser.getId(), savedUser.getEmail());

        Cart cart = cartService.createCart(savedUser);
        log.info("Cart created successfully for userId={}", savedUser.getId());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        log.info("JWT token generated successfully for email={}", email);

        AuthResponse authResponse = new AuthResponse(token, "Signup Success");

        log.info("Signup completed successfully for email={}", email);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
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
