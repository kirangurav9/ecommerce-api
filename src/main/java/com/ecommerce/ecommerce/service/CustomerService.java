package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Page<Customer> getAllCustomers(Pageable pageable);
    Customer getCustomer(Long id);
    void deleteCustomer(Long id);
}
