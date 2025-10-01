package com.example.rental.service;

import com.example.rental.entity.Reservation;
import com.example.rental.entity.ReservationStatus;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    // Tạo phiếu đặt phòng mới (Đầu tiên sẽ là PENDING)
    Reservation createReservation(Reservation reservation);

    // Lấy phiếu đặt phòng theo ID
    Optional<Reservation> findById(Long id);

    // Lấy danh sách phiếu theo ID người thuê
    List<Reservation> findReservationsByTenantId(Long tenantId);

    // Lấy danh sách phiếu theo trạng thái
    List<Reservation> findReservationsByStatus(ReservationStatus status);

    // Xác nhận giữ phòng (Chuyển từ PENDING sang HOLD)
    Reservation confirmReservation(Long reservationId);
    
    // Hủy phiếu đặt phòng (Chuyển sang CANCELLED)
    Reservation cancelReservation(Long reservationId);
}