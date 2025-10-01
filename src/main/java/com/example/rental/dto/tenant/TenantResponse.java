package com.example.rental.dto.tenant;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TenantResponse {
    private Long id;
    private String tenantCode;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String cccd;
    private String studentId;
    private String university;
    private String address;
    private String status;
    private LocalDateTime createdAt;
}