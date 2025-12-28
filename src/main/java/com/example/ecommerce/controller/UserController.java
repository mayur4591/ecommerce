package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {

        log.info("Fetch user profile request received");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("User profile fetched successfully for userId={}", user.getId());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
