package com.example.rental.entity;

/**
 * Trạng thái đặt phòng: PENDING_CONFIRMATION, RESERVED, CANCELLED, COMPLETED
 */
public enum ReservationStatus {
    PENDING_CONFIRMATION, // Ban đầu, sau khi người thuê nhấn "Đặt trước" [cite: 61]
    RESERVED,             // Đã giữ phòng (Nghiệp vụ: sau khi xác nhận [cite: 63])
    CANCELLED,            // Bị hủy
    COMPLETED             // Hoàn thành (khi chuyển thành Hợp đồng)
}