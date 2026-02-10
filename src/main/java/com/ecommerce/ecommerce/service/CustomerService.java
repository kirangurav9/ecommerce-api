package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.Customer;
import java.util.List;

public interface CustomerService {

    Customer createCustomer(Customer customer);

    Customer getCustomer(Long id);

    List<Customer> getAllCustomers();

    void deleteCustomer(Long id);
}
