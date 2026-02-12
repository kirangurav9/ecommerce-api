package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.DtoMapper;
import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/customer/{customerId}")
    public OrderDTO createOrder(@PathVariable Long customerId, @Valid @RequestBody OrderDTO orderDTO) {
        Order order = new Order();
        order.setAmount(orderDTO.getAmount());
        order.setStatus(orderDTO.getStatus());
        order.setOrderDate(orderDTO.getOrderDate());

        Order saved = service.createOrder(customerId, order);
        return DtoMapper.toOrderDTO(saved);
    }

    @GetMapping("/customer/{customerId}")
    public Page<OrderDTO> getOrdersForCustomer(
            @PathVariable Long customerId,
            @RequestParam int page,
            @RequestParam int size) {

        Page<Order> orders = service.getOrdersForCustomer(customerId, page, size);
        return orders.map(DtoMapper::toOrderDTO);
    }

    @GetMapping("/filter")
    public List<OrderDTO> filterOrders(
            @RequestParam String start,
            @RequestParam String end) {

        return service.filterOrders(
                java.time.LocalDate.parse(start),
                java.time.LocalDate.parse(end)
        ).stream()
         .map(DtoMapper::toOrderDTO)
         .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public OrderDTO updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        Order updated = service.updateOrder(id, orderDTO);
        return DtoMapper.toOrderDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
    }
}
