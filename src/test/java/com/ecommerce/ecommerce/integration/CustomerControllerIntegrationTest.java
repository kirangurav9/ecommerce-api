package com.ecommerce.ecommerce.integration;

import com.ecommerce.ecommerce.dto.CustomerDTO;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(com.ecommerce.ecommerce.controller.CustomerController.class)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice Smith");
        customer.setEmail("alice@example.com");
        customer.setPhone("0871234567");
        customer.setAddress("123 Main St");
    }

    @Test
    void createCustomer_returns200WithCustomerDTO() throws Exception {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getCustomer_returns200_whenFound() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(customer);

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice Smith"));
    }

    @Test
    void getCustomer_returns404_whenNotFound() throws Exception {
        when(customerService.getCustomer(99L))
                .thenThrow(new CustomerNotFoundException("Customer not found with id: 99"));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Customer not found with id: 99"));
    }

    @Test
    void getAllCustomers_returnsPagedResults() throws Exception {
        when(customerService.getAllCustomers(PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(customer)));

        mockMvc.perform(get("/customers?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Alice Smith"));
    }

    @Test
    void deleteCustomer_returns200_whenExists() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isOk());

        verify(customerService).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_returns404_whenNotFound() throws Exception {
        doThrow(new CustomerNotFoundException("Customer not found with id: 99"))
                .when(customerService).deleteCustomer(99L);

        mockMvc.perform(delete("/customers/99"))
                .andExpect(status().isNotFound());
    }
}
