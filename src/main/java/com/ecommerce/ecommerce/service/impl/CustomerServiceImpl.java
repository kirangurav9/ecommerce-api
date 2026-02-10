package com.ecommerce.ecommerce.service.impl;

import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.repository.CustomerRepository;
import com.ecommerce.ecommerce.service.CustomerService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        return repo.save(customer);
    }

    @Override
    public Customer getCustomer(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!repo.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
