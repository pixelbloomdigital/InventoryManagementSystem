package com.pixelbloom.auth_server.dto;

import com.pixelbloom.auth_server.enums.CustomerStatus;
import com.pixelbloom.auth_server.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerRegistrationRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role; // put if no role given default to USER

    // private CustomerStatus status;

 //   LocalDateTime createdAt;



}
