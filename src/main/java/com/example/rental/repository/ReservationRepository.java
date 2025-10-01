package com.example.rental.repository;

import com.example.rental.entity.Reservation;
import com.example.rental.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Tìm đơn đặt phòng theo ID người thuê
    List<Reservation> findByTenantId(Long tenantId);

    // Tìm đơn đặt phòng theo trạng thái
    List<Reservation> findByStatus(ReservationStatus status);
}