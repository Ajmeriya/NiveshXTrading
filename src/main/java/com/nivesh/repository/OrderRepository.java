package com.nivesh.repository;

import java.util.List;
import java.util.Optional;

import com.nivesh.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long orderId);
}
