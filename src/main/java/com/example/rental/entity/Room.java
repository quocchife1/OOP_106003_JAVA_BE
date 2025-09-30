package com.example.rental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "room_number")
    private String roomNumber;

    private BigDecimal price;
    private String status;
    private Double area;
    private LocalDateTime created_at;

    // Getters and setters
}
