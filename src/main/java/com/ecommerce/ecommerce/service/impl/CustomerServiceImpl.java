package com.ecommerce.ecommerce.service.impl;

import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.repository.CustomerRepository;
import com.ecommerce.ecommerce.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!repo.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
