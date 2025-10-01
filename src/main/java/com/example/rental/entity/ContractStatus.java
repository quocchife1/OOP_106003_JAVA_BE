package com.example.rental.entity;

/**
 * Trạng thái hợp đồng: PENDING, ACTIVE, ENDED, CANCELLED
 */
public enum ContractStatus {
    PENDING,    // Đang chờ ký/xác nhận
    ACTIVE,     // Đang có hiệu lực
    ENDED,      // Đã kết thúc
    CANCELLED   // Đã hủy
}