package com.example.rental.repository;

import com.example.rental.entity.Reservation;
import com.example.rental.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // Tìm danh sách theo Tenant ID
    List<Reservation> findByTenantId(Long tenantId);
    Page<Reservation> findByTenantId(Long tenantId, Pageable pageable);

    // Tìm danh sách theo Room ID
    List<Reservation> findByRoomId(Long roomId);

    // Tìm danh sách theo Trạng thái
    List<Reservation> findByStatus(ReservationStatus status);
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}