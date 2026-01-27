package com.pixelbloom.auth_server.controller;

import com.pixelbloom.auth_server.dto.CustomerDetailsResponse;
import com.pixelbloom.auth_server.dto.CustomerRegistrationRequest;
import com.pixelbloom.auth_server.dto.LoginRequest;
import com.pixelbloom.auth_server.enums.CustomerStatus;
import com.pixelbloom.auth_server.enums.Role;
import com.pixelbloom.auth_server.model.Customer;
import com.pixelbloom.auth_server.service.CustomerRegistrationService;
import com.pixelbloom.auth_server.service.CustomerService;
import com.pixelbloom.auth_server.service.CustomerUserDetailsService;
import com.pixelbloom.auth_server.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomerService customerService;
    private final CustomerRegistrationService registrationService;
    //private final CustomerUserDetailsService customerUserDetailsService;

    /**
     * Step 1: Customer Registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        try {
            Customer customer = registrationService.registerCustomer(request);

            // Set default role if not provided
            if (request.getRole() == null) {
                customer.setRole(Role.USER);
            }

            return ResponseEntity.ok(Map.of(
                    "customerId", customer.getId(),
                    "role", customer.getRole().name(),
                    "message", "Registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Step 2: Customer Login & Token Generation
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest request) {

        Customer customer = customerService.findByEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is blocked"));
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(
                customer.getEmail(),
                customer.getRole().name(),
                customer.getId()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", customer.getRole().name(),
                "customerId", customer.getId()
        ));
    }


    /**
     * Get customer details by ID (for internal service calls)
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerDetailsResponse> getCustomerDetails(@PathVariable Long customerId) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerDetailsResponse response = CustomerDetailsResponse.builder()
                .customerId(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .build();

        return ResponseEntity.ok(response);
    }
}
