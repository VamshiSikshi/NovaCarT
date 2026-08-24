package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(
            Long orderId,
            Long userId
    );

    Page<Order> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<Order> findByStatusOrderByCreatedAtDesc(
            OrderStatus status,
            Pageable pageable
    );

    long countByStatus(OrderStatus status);

    @EntityGraph(attributePaths = {"items", "items.product"})
    @Query("""
        SELECT DISTINCT o
        FROM Order o
        WHERE o.user.id = :userId
        ORDER BY o.createdAt DESC
        """)
    List<Order> findMyOrdersWithItems(
            @Param("userId") Long userId
    );

    @Query("""
        SELECT new com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto(
            o.id,
            u.id,
            u.username,
            o.status,
            o.totalAmount,
            o.createdAt
        )
        FROM Order o
        JOIN o.user u
        ORDER BY o.createdAt DESC
        """)
    Page<AdminOrderSummaryDto> findAllAdminOrderSummaries(
            Pageable pageable
    );

    @Query("""
        SELECT new com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto(
            o.id,
            u.id,
            u.username,
            o.status,
            o.totalAmount,
            o.createdAt
        )
        FROM Order o
        JOIN o.user u
        WHERE o.status = :status
        ORDER BY o.createdAt DESC
        """)
    Page<AdminOrderSummaryDto> findAdminOrderSummariesByStatus(
            @Param("status") OrderStatus status,
            Pageable pageable
    );
}