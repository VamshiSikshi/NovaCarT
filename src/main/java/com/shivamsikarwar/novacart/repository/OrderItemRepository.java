package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
