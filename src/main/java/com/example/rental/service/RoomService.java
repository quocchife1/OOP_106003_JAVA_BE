package com.example.rental.service;

import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    // Lấy phòng theo ID
    Optional<Room> findById(Long id);

    // Lấy phòng theo mã code
    Optional<Room> findByRoomCode(String roomCode);

    // Lấy tất cả phòng trong một chi nhánh
    List<Room> findRoomsByBranchId(Long branchId);
    
    // Lấy phòng theo trạng thái (VD: AVAILABLE, OCCUPIED)
    List<Room> findRoomsByStatus(RoomStatus status);

    // Tạo/Cập nhật phòng
    Room save(Room room);
}