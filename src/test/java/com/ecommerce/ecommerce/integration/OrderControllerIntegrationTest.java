package com.ecommerce.ecommerce.integration;

import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.ecommerce.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(com.ecommerce.ecommerce.controller.OrderController.class)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private ObjectMapper objectMapper;
    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Alice Smith");

        order = new Order();
        order.setId(10L);
        order.setAmount(150.00);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDate.of(2024, 6, 1));
        order.setCustomer(customer);

        orderDTO = new OrderDTO(10L, LocalDate.of(2024, 6, 1), 150.00, "PENDING");
    }

    @Test
    void createOrder_returns200WithOrderDTO() throws Exception {
        when(orderService.createOrder(eq(1L), any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_returns404_whenCustomerNotFound() throws Exception {
        when(orderService.createOrder(eq(99L), any(Order.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found with id: 99"));

        mockMvc.perform(post("/orders/customer/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersForCustomer_returnsPagedOrders() throws Exception {
        when(orderService.getOrdersForCustomer(1L, 0, 5))
                .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/orders/customer/1?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void filterOrders_returnsListOfOrders() throws Exception {
        when(orderService.filterOrders(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/orders/filter?start=2024-01-01&end=2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(150.00));
    }

    @Test
    void updateOrder_returns200WithUpdatedOrder() throws Exception {
        OrderDTO updatedDTO = new OrderDTO(10L, LocalDate.of(2024, 9, 1), 300.00, "SHIPPED");
        order.setAmount(300.00);
        order.setStatus("SHIPPED");
        when(orderService.updateOrder(eq(10L), any(OrderDTO.class))).thenReturn(order);

        mockMvc.perform(put("/orders/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"))
                .andExpect(jsonPath("$.amount").value(300.00));
    }

    @Test
    void updateOrder_returns404_whenOrderNotFound() throws Exception {
        when(orderService.updateOrder(eq(99L), any(OrderDTO.class)))
                .thenThrow(new OrderNotFoundException("Order not found with id: 99"));

        mockMvc.perform(put("/orders/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrder_returns200_whenExists() throws Exception {
        doNothing().when(orderService).deleteOrder(10L);

        mockMvc.perform(delete("/orders/10"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrder_returns404_whenNotFound() throws Exception {
        doThrow(new OrderNotFoundException("Order not found with id: 99"))
                .when(orderService).deleteOrder(99L);

        mockMvc.perform(delete("/orders/99"))
                .andExpect(status().isNotFound());
    }
}
