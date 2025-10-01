package com.example.rental.repository;

import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tìm phòng theo mã phòng
    Room findByRoomCode(String roomCode);

    // Tìm phòng theo ID chi nhánh
    List<Room> findByBranchId(Long branchId);

    // Tìm phòng theo trạng thái
    List<Room> findByStatus(RoomStatus status);
}