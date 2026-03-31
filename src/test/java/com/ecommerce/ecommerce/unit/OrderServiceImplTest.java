package com.ecommerce.ecommerce.unit;

import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.ecommerce.repository.CustomerRepository;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private CustomerRepository customerRepo;

    @InjectMocks
    private OrderServiceImpl service;

    private Customer customer;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice Smith");

        order = new Order();
        order.setId(10L);
        order.setAmount(150.00);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDate.of(2024, 6, 1));
        order.setCustomer(customer);
    }

    // --- createOrder ---

    @Test
    void createOrder_linksCustomerAndSavesOrder() {
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        when(orderRepo.save(order)).thenReturn(order);

        Order result = service.createOrder(1L, order);

        assertThat(result.getCustomer()).isEqualTo(customer);
        verify(orderRepo).save(order);
    }

    @Test
    void createOrder_throwsCustomerNotFoundException_whenCustomerMissing() {
        when(customerRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOrder(99L, order))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepo, never()).save(any());
    }

    // --- getOrdersForCustomer ---

    @Test
    void getOrdersForCustomer_returnsPage_whenCustomerExists() {
        when(customerRepo.existsById(1L)).thenReturn(true);
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepo.findAll(PageRequest.of(0, 5))).thenReturn(page);

        Page<Order> result = service.getOrdersForCustomer(1L, 0, 5);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getOrdersForCustomer_throwsCustomerNotFoundException_whenCustomerMissing() {
        when(customerRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.getOrdersForCustomer(99L, 0, 5))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- filterOrders ---

    @Test
    void filterOrders_returnOrdersWithinDateRange() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        when(orderRepo.findByOrderDateBetween(start, end)).thenReturn(List.of(order));

        List<Order> result = service.filterOrders(start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderDate()).isEqualTo(LocalDate.of(2024, 6, 1));
    }

    @Test
    void filterOrders_returnsEmpty_whenNoOrdersInRange() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        when(orderRepo.findByOrderDateBetween(start, end)).thenReturn(List.of());

        List<Order> result = service.filterOrders(start, end);

        assertThat(result).isEmpty();
    }

    // --- deleteOrder ---

    @Test
    void deleteOrder_deletesSuccessfully_whenExists() {
        when(orderRepo.existsById(10L)).thenReturn(true);
        doNothing().when(orderRepo).deleteById(10L);

        assertThatCode(() -> service.deleteOrder(10L)).doesNotThrowAnyException();
        verify(orderRepo).deleteById(10L);
    }

    @Test
    void deleteOrder_throwsOrderNotFoundException_whenNotFound() {
        when(orderRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteOrder(99L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepo, never()).deleteById(any());
    }

    // --- updateOrder ---

    @Test
    void updateOrder_updatesFieldsAndSaves() {
        OrderDTO dto = new OrderDTO(null, LocalDate.of(2024, 9, 15), 300.00, "SHIPPED");
        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(orderRepo.save(order)).thenReturn(order);

        Order result = service.updateOrder(10L, dto);

        assertThat(result.getAmount()).isEqualTo(300.00);
        assertThat(result.getStatus()).isEqualTo("SHIPPED");
        assertThat(result.getOrderDate()).isEqualTo(LocalDate.of(2024, 9, 15));
    }

    @Test
    void updateOrder_throwsOrderNotFoundException_whenNotFound() {
        OrderDTO dto = new OrderDTO(null, LocalDate.now(), 100.0, "PENDING");
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateOrder(99L, dto))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("99");
    }
}
