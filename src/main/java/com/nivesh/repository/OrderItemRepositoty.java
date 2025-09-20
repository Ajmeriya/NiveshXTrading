package com.nivesh.repository;

import com.nivesh.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepositoty extends JpaRepository<OrderItem,Long> {


}
