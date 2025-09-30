package com.example.rental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "lease_id")
    private Long leaseId;

    private BigDecimal amount;
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    private String method;
    private String status;

    // Getters and setters
}
