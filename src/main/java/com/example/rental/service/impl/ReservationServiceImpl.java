package com.example.rental.service.impl;

import com.example.rental.entity.Reservation;
import com.example.rental.entity.ReservationStatus;
import com.example.rental.entity.RoomStatus;
import com.example.rental.repository.ReservationRepository;
import com.example.rental.repository.RoomRepository;
import com.example.rental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository; 

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // 1. Kiểm tra phòng phải đang là AVAILABLE
        if (reservation.getRoom().getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Phòng này hiện đã được thuê hoặc đang được giữ.");
        }
        
        // 2. Thiết lập trạng thái ban đầu và thời gian hết hạn (ví dụ: 3 ngày)
        reservation.setStatus(ReservationStatus.PENDING_CONFIRMATION);
        // reservation.setExpirationDate(LocalDateTime.now().plusDays(3)); // Có thể dùng logic này
        
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> findReservationsByTenantId(Long tenantId) {
        return reservationRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Reservation> findReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu đặt phòng."));
        
        if (reservation.getStatus() != ReservationStatus.PENDING_CONFIRMATION) {
            throw new RuntimeException("Chỉ phiếu đặt phòng PENDING mới có thể được xác nhận.");
        }
        
        // 1. Chuyển trạng thái sang HOLD
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setExpirationDate(LocalDateTime.now().plusDays(1)); // Ví dụ: Giữ phòng 3 ngày
        
        // 2. Cập nhật trạng thái phòng thành RESERVED
        reservation.getRoom().setStatus(RoomStatus.RESERVED);
        roomRepository.save(reservation.getRoom());
        
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu đặt phòng."));

        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Phiếu đặt phòng đã kết thúc hoặc đã bị hủy.");
        }
        
        // Nếu đang giữ phòng, giải phóng phòng
        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            reservation.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(reservation.getRoom());
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }
}