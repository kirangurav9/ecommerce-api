package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.CustomerDTO;
import com.ecommerce.ecommerce.dto.DtoMapper;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public CustomerDTO createCustomer(@RequestBody Customer customer) {
        Customer saved = service.createCustomer(customer);
        return DtoMapper.toCustomerDTO(saved);
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return service.getAllCustomers()
                .stream()
                .map(DtoMapper::toCustomerDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable Long id) {
        Customer customer = service.getCustomer(id);
        return DtoMapper.toCustomerDTO(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        service.deleteCustomer(id);
    }
}
