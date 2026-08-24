package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder_Id(Long orderId);

    boolean existsByOrder_Id(Long orderId);

    Optional<Payment> findByTransactionId(
            String transactionId
    );

    long countByStatus(PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
            """)
    BigDecimal getTotalAmountByStatus(
            @Param("status") PaymentStatus status
    );

    @Query("""
            SELECT p
            FROM Payment p
            WHERE (:status IS NULL OR p.status = :status)
            AND (:method IS NULL OR p.method = :method)
            ORDER BY p.createdAt DESC
            """)
    Page<Payment> findAdminPayments(
            @Param("status") PaymentStatus status,
            @Param("method") PaymentMethod method,
            Pageable pageable
    );
}