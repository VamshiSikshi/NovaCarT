package com.shivamsikarwar.novacart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_status_created",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_payment_method",
                        columnList = "method"
                )
        }
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(
            name="order_id",
            nullable = false,
            unique = true
    )
    private Order order;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(unique = true)
    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = PaymentStatus.PENDING;
        }
   }
}
