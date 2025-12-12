package com.example.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Báo cáo hư hỏng phòng (Damage Report)
 * Được tạo khi khách hàng trả phòng để đánh giá tình trạng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "damage_reports")
public class DamageReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hợp đồng được trả phòng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    /**
     * Nhân viên/Quản lý kiểm tra hư hỏng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id", nullable = false)
    private Employees inspector;

    /**
     * Mô tả tổng quát tình trạng phòng
     */
    @Lob
    @Column(nullable = false)
    private String description;

    /**
     * Chi tiết từng hư hỏng (JSON format)
     * VD: [{"item": "Cửa sổ", "damage": "Vỡ", "cost": 500000}, ...]
     */
    @Lob
    private String damageDetails;

    /**
     * Tổng chi phí sửa chữa (do hư hỏng)
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDamageCost;

    /**
     * Trạng thái báo cáo: DRAFT, SUBMITTED, APPROVED, REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private DamageReportStatus status = DamageReportStatus.DRAFT;

    /**
     * Người phê duyệt báo cáo
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employees approver;

    /**
     * Ghi chú khi phê duyệt/từ chối
     */
    @Lob
    private String approverNote;

    /**
     * Hình ảnh chứng minh hư hỏng
     */
    @OneToMany(mappedBy = "damageReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DamageImage> images;

    /**
     * Ngày tạo báo cáo
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Ngày phê duyệt
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
