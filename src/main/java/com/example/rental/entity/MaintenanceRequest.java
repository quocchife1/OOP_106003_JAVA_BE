package com.example.rental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenancerequests")
public class MaintenanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "tenant_id")
    private Long tenantId;

    private String description;
    private String status;
    private LocalDateTime created_at;

    // Getters and setters
}
