package com.example.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants")
public class Tenant extends BaseEntity { // Giả định BaseEntity chứa created_at và updated_at
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã người thuê (VD: T001).
     * VARCHAR(10), UNIQUE, NOT NULL.
     */
    @Column(name = "tenant_code", unique = true, nullable = false, length = 10)
    private String tenantCode;

    /**
     * Tên đăng nhập.
     * VARCHAR(50), UNIQUE, NOT NULL.
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Mật khẩu.
     * VARCHAR(255), NOT NULL.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Họ và tên đầy đủ.
     * VARCHAR(100), NOT NULL.
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * Email.
     * VARCHAR(100), UNIQUE (Có thể NULL).
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * Số điện thoại.
     * VARCHAR(20). (Có thể NULL theo SQL của bạn).
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    /**
     * URL ảnh đại diện.
     * VARCHAR(255).
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /**
     * Số CCCD/CMND.
     * VARCHAR(20), UNIQUE.
     */
    @Column(unique = true, length = 20)
    private String cccd;
    
    /**
     * Mã số sinh viên (MSSV).
     * VARCHAR(20).
     */
    @Column(name = "student_id", length = 20)
    private String studentId;
    
    /**
     * Tên trường đại học.
     * VARCHAR(100).
     */
    @Column(length = 100)
    private String university;
    
    /**
     * Địa chỉ.
     * VARCHAR(255).
     */
    @Column(length = 255)
    private String address;

    /**
     * Trạng thái người dùng (ACTIVE/BANNED).
     * ENUM.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20) // Độ dài nên đặt cho ENUM String
    private UserStatus status;
}