package com.example.rental.mapper;

import com.example.rental.dto.room.RoomResponse;
import com.example.rental.entity.Room;

public class RoomMapper {

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .branchCode(room.getBranchCode())
                .roomNumber(room.getRoomNumber())
                .area(room.getArea())
                .price(room.getPrice())
                .status(room.getStatus())
                .description(room.getDescription())
                .build();
    }
}
