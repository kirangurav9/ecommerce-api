package com.ecommerce.ecommerce.service;

import com.ecommerce.ecommerce.entity.Order;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    Order createOrder(Long customerId, Order order);

    Page<Order> getOrdersForCustomer(Long customerId, int page, int size);

    List<Order> filterOrders(LocalDate start, LocalDate end);

    void deleteOrder(Long id);
}
