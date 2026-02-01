package com.pixelbloom.auth_server.serviceImpl;

import com.pixelbloom.auth_server.dto.CustomerRegistrationRequest;
import com.pixelbloom.auth_server.enums.CustomerStatus;
import com.pixelbloom.auth_server.enums.Role;
import com.pixelbloom.auth_server.model.Customer;
import com.pixelbloom.auth_server.repository.CustomerRepository;
import com.pixelbloom.auth_server.service.CustomerRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerRegistrationServiceImpl(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer registerCustomer(CustomerRegistrationRequest request) {

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Customer customer = Customer.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(CustomerStatus.ACTIVE)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return customerRepository.save(customer);
    }
}

