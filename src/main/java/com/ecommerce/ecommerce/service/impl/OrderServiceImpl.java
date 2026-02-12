package com.ecommerce.ecommerce.service.impl;

import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.exception.CustomerNotFoundException;
import com.ecommerce.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.ecommerce.repository.CustomerRepository;
import com.ecommerce.ecommerce.repository.OrderRepository;
import com.ecommerce.ecommerce.service.OrderService;
import com.ecommerce.ecommerce.dto.OrderDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;

    public OrderServiceImpl(OrderRepository orderRepo, CustomerRepository customerRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public Order createOrder(Long customerId, Order order) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        order.setCustomer(customer);
        return orderRepo.save(order);
    }

    @Override
    public Page<Order> getOrdersForCustomer(Long customerId, int page, int size) {
        if (!customerRepo.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        return orderRepo.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<Order> filterOrders(LocalDate start, LocalDate end) {
        return orderRepo.findByOrderDateBetween(start, end);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        orderRepo.deleteById(id);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Order existing = orderRepo.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        existing.setOrderDate(orderDTO.getOrderDate());
        existing.setAmount(orderDTO.getAmount());
        existing.setStatus(orderDTO.getStatus());

        return orderRepo.save(existing);
    }
}
