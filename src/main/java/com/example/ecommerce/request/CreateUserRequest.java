package com.example.ecommerce.request;

import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.PaymentInformation;
import com.example.ecommerce.entity.Rating;
import com.example.ecommerce.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    private Long id;

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String role;

    private String mobile;

    private List<Address> addresses =new ArrayList<>();

    private List<PaymentInformation> paymentInformation=new ArrayList<>();

    private List<Rating> ratings = new ArrayList<>();

    private List<Review> reviews = new ArrayList<>();

    private LocalDateTime createdAt;

}
