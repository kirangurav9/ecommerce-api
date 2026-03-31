package com.ecommerce.ecommerce.unit;

import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.repository.CustomerRepository;
import com.ecommerce.ecommerce.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repo;

    @InjectMocks
    private CustomerServiceImpl service;

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

    // --- createCustomer ---

    @Test
    void createCustomer_savesAndReturnsCustomer() {
        when(repo.save(customer)).thenReturn(customer);

        Customer result = service.createCustomer(customer);

        assertThat(result).isEqualTo(customer);
        verify(repo, times(1)).save(customer);
    }

    @Test
    void createCustomer_persistsAllFields() {
        when(repo.save(customer)).thenReturn(customer);

        Customer result = service.createCustomer(customer);

        assertThat(result.getName()).isEqualTo("Alice Smith");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
    }

    // --- getCustomer ---

    @Test
    void getCustomer_returnsCustomer_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(customer));

        Customer result = service.getCustomer(1L);

        assertThat(result).isEqualTo(customer);
    }

    @Test
    void getCustomer_throwsCustomerNotFoundException_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCustomer(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getAllCustomers ---

    @Test
    void getAllCustomers_returnsPageOfCustomers() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(repo.findAll(pageable)).thenReturn(page);

        Page<Customer> result = service.getAllCustomers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(customer);
    }

    @Test
    void getAllCustomers_returnsEmptyPage_whenNoCustomers() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repo.findAll(pageable)).thenReturn(Page.empty());

        Page<Customer> result = service.getAllCustomers(pageable);

        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    // --- deleteCustomer ---

    @Test
    void deleteCustomer_deletesSuccessfully_whenExists() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        assertThatCode(() -> service.deleteCustomer(1L)).doesNotThrowAnyException();
        verify(repo).deleteById(1L);
    }

    @Test
    void deleteCustomer_throwsCustomerNotFoundException_whenNotFound() {
        when(repo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteCustomer(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");

        verify(repo, never()).deleteById(any());
    }
}
