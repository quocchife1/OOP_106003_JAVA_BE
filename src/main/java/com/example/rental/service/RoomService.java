package com.example.rental.service;

import com.example.rental.dto.room.RoomRequest;
import com.example.rental.dto.room.RoomResponse;
import com.example.rental.entity.RoomStatus;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);
    RoomResponse getRoomById(Long id);
    RoomResponse getRoomByCode(String roomCode);
    List<RoomResponse> getRoomsByBranchCode(String branchCode);
    List<RoomResponse> getRoomsByStatus(RoomStatus status);
}
