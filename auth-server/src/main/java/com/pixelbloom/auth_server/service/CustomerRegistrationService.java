package com.pixelbloom.auth_server.service;

import com.pixelbloom.auth_server.dto.CustomerRegistrationRequest;
import com.pixelbloom.auth_server.model.Customer;

public interface CustomerRegistrationService {
    Customer registerCustomer(CustomerRegistrationRequest request);
}
