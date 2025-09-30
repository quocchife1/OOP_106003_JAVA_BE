package com.example.rental.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    private String name;
    private String address;
    private String type;

    @Column(name = "owner_id")
    private Long ownerId;

    private String description;
    private LocalDateTime created_at;

    // Getters and setters
}
