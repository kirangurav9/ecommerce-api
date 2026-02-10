package com.ecommerce.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.ecommerce.entity.Order;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // For date filtering
    List<Order> findByOrderDateBetween(LocalDate start, LocalDate end);
}
