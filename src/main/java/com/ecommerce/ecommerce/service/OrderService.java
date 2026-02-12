package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.dto.OrderDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface OrderService {
    Order createOrder(Long customerId, Order order);
    Page<Order> getOrdersForCustomer(Long customerId, int page, int size);
    List<Order> filterOrders(LocalDate start, LocalDate end);
    void deleteOrder(Long id);
    Order updateOrder(Long id, OrderDTO orderDTO); // <-- Added method
}
