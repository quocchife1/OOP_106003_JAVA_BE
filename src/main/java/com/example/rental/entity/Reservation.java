package com.example.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations")
public class Reservation { // Bảng này không có updated_at trong SQL, nên không kế thừa BaseEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_code", unique = true, nullable = false, length = 20)
    private String reservationCode;

    // QUAN HỆ: Người thuê đặt phòng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // QUAN HỆ: Phòng được đặt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status; // Sử dụng Enum ReservationStatus

    @CreationTimestamp
    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate; // Thời gian hết hạn giữ phòng

    @Lob
    private String notes;
}