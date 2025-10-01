package com.example.rental.service.impl;

import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import com.example.rental.repository.RoomRepository;
import com.example.rental.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Optional<Room> findByRoomCode(String roomCode) {
        // Giả định RoomRepository.findByRoomCode đã được sửa thành Optional<Room>
        // Nếu chưa sửa, cần dùng Optional.ofNullable(roomRepository.findByRoomCode(roomCode))
        Room room = roomRepository.findByRoomCode(roomCode);
        return Optional.ofNullable(room);
    }

    @Override
    public List<Room> findRoomsByBranchId(Long branchId) {
        return roomRepository.findByBranchId(branchId);
    }

    @Override
    public List<Room> findRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    @Override
    public Room save(Room room) {
        // Logic nghiệp vụ: Đảm bảo phòng thuộc một chi nhánh tồn tại
        // Logic nghiệp vụ: Kiểm tra trùng RoomCode
        return roomRepository.save(room);
    }
}