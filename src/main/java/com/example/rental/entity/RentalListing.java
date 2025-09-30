package com.example.rental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentallistings")
public class RentalListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long id;

    @Column(name = "property_id")
    private Long propertyId;

    private String title;
    private String description;
    private BigDecimal price;
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "contact_phone")
    private String contactPhone;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String status;

    // Getters and setters
}
