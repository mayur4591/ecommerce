package com.example.ecommerce.service.Impl;

import com.example.ecommerce.config.JwtProvider;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User findUserById(Long userId) throws UserException {

        log.info("Finding user by userId={}", userId);

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            log.info("User found successfully with userId={}", userId);
            return user.get();
        }

        log.warn("User not found with userId={}", userId);
        throw new UserException("User not found with id " + userId);
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {

        log.info("Finding user profile using JWT");

        String email = jwtProvider.getEmailFromToken(jwt);
        log.debug("Email extracted from JWT");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("User not found for extracted email");
            throw new UserException("User not found with email " + email);
        }

        log.info("User profile found successfully for userId={}", user.getId());
        return user;
    }
}
