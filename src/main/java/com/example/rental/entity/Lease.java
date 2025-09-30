package com.example.rental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leases")
public class Lease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lease_id")
    private Long id;

    @Column(name = "lease_type")
    private String leaseType;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "rent_amount")
    private BigDecimal rentAmount;

    private String status;
    private LocalDateTime created_at;

    // Getters and setters
}
