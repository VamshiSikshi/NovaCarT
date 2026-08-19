package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder_Id(Long orderId);

    boolean existsByOrder_Id(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);

}
