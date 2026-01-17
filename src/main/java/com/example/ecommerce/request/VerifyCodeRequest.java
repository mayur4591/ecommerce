package com.example.ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeRequest {
    private String email;
    private String password;
    private String verificationCode;
    private String firstName;
    private String lastName;
    private String role;
}
