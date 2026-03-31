package com.ecommerce.ecommerce.unit;

import com.ecommerce.ecommerce.dto.CustomerDTO;
import com.ecommerce.ecommerce.dto.DtoMapper;
import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.entity.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    @Test
    void toCustomerDTO_mapsAllFields() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice");
        customer.setEmail("alice@example.com");
        customer.setOrders(List.of(new Order(), new Order()));

        CustomerDTO dto = DtoMapper.toCustomerDTO(customer);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getTotalOrders()).isEqualTo(2);
    }

    @Test
    void toCustomerDTO_returnsTotalOrdersZero_whenOrdersNull() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setName("Bob");
        customer.setEmail("bob@example.com");
        customer.setOrders(null);

        CustomerDTO dto = DtoMapper.toCustomerDTO(customer);

        assertThat(dto.getTotalOrders()).isEqualTo(0);
    }

    @Test
    void toOrderDTO_mapsAllFields() {
        Order order = new Order();
        order.setId(10L);
        order.setOrderDate(LocalDate.of(2024, 5, 20));
        order.setAmount(99.99);
        order.setStatus("PENDING");

        OrderDTO dto = DtoMapper.toOrderDTO(order);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getOrderDate()).isEqualTo(LocalDate.of(2024, 5, 20));
        assertThat(dto.getAmount()).isEqualTo(99.99);
        assertThat(dto.getStatus()).isEqualTo("PENDING");
    }
}
