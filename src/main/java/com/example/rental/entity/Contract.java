package com.example.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // QUAN HỆ: Người thuê (Tenant) - Bắt buộc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // QUAN HỆ: Phòng (Room) - Bắt buộc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // Ngày bắt đầu thuê (DATE)

    @Column(name = "end_date")
    private LocalDate endDate; // Ngày kết thúc thuê (DATE)

    @Column(precision = 12, scale = 2)
    private BigDecimal deposit; // Tiền đặt cọc

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ContractStatus status; // Sử dụng Enum ContractStatus

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Bảng SQL không có updated_at, nên không kế thừa BaseEntity và chỉ dùng CreationTimestamp
}