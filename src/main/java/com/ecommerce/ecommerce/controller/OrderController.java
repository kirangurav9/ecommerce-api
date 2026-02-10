package com.ecommerce.ecommerce.controller;

import com.ecommerce.ecommerce.dto.DtoMapper;
import com.ecommerce.ecommerce.dto.OrderDTO;
import com.ecommerce.ecommerce.dto.OrderRequest;
import com.ecommerce.ecommerce.entity.Order;
import com.ecommerce.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // -------------------------
    // CREATE ORDER (UPDATED)
    // -------------------------
    @PostMapping("/customer/{customerId}")
    public OrderDTO createOrder(@PathVariable Long customerId, @RequestBody OrderRequest request) {

        Order order = new Order();
        order.setAmount(request.getAmount());
        order.setStatus(request.getStatus());
        order.setOrderDate(LocalDate.now());

        Order saved = service.createOrder(customerId, order);
        return DtoMapper.toOrderDTO(saved);
    }

    // -------------------------
    // GET ORDERS FOR CUSTOMER
    // -------------------------
    @GetMapping("/customer/{customerId}")
    public Page<OrderDTO> getOrdersForCustomer(
            @PathVariable Long customerId,
            @RequestParam int page,
            @RequestParam int size) {

        Page<Order> orders = service.getOrdersForCustomer(customerId, page, size);
        return orders.map(DtoMapper::toOrderDTO);
    }

    // -------------------------
    // FILTER ORDERS BY DATE
    // -------------------------
    @GetMapping("/filter")
    public List<OrderDTO> filterOrders(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        return service.filterOrders(startDate, endDate)
                .stream()
                .map(DtoMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    // -------------------------
    // DELETE ORDER
    // -------------------------
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
    }
}
