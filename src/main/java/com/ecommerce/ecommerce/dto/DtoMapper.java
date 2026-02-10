package com.ecommerce.ecommerce.dto;

import com.ecommerce.ecommerce.entity.Customer;
import com.ecommerce.ecommerce.entity.Order;

public class DtoMapper {

    // Convert Customer → CustomerDTO
    public static CustomerDTO toCustomerDTO(Customer customer) {
        int totalOrders = (customer.getOrders() == null) ? 0 : customer.getOrders().size();

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                totalOrders
        );
    }

    // Convert Order → OrderDTO
    public static OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getAmount(),
                order.getStatus()
        );
    }
}
