package com.pixelbloom.auth_server.serviceImpl;

import com.pixelbloom.auth_server.dto.CustomerRegistrationRequest;
import com.pixelbloom.auth_server.enums.CustomerStatus;
import com.pixelbloom.auth_server.enums.Role;
import com.pixelbloom.auth_server.model.Customer;
import com.pixelbloom.auth_server.repository.CustomerRepository;
import com.pixelbloom.auth_server.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void updateStatus(Long customerId, CustomerStatus status) {
        Customer customer = findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setStatus(status);
        customerRepository.save(customer);
    }
}