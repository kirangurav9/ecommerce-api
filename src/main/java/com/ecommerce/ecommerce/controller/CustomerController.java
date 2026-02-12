package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.CustomerDTO;
import com.ecommerce.ecommerce.dto.DtoMapper;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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
    public Page<CustomerDTO> getAllCustomers(
            @RequestParam int page,
            @RequestParam int size) {

        return service.getAllCustomers(PageRequest.of(page, size))
                      .map(DtoMapper::toCustomerDTO);
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
