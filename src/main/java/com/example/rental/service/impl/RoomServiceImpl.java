package com.example.rental.service.impl;

import com.example.rental.dto.room.RoomRequest;
import com.example.rental.dto.room.RoomResponse;
import com.example.rental.entity.Branch;
import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import com.example.rental.mapper.RoomMapper;
import com.example.rental.repository.BranchRepository;
import com.example.rental.repository.RoomRepository;
import com.example.rental.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        Branch branch = branchRepository.findByBranchCode(request.getBranchCode())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

        String roomCode = branch.getBranchCode() + request.getRoomNumber();

        Room room = Room.builder()
                .roomCode(roomCode)
                .branch(branch)
                .branchCode(branch.getBranchCode())
                .roomNumber(request.getRoomNumber())
                .area(request.getArea())
                .price(request.getPrice())
                .status(request.getStatus())
                .description(request.getDescription())
                .build();

        return RoomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));

        Branch branch = branchRepository.findByBranchCode(request.getBranchCode())
                .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

        String roomCode = branch.getBranchCode() + request.getRoomNumber();

        room.setRoomCode(roomCode);
        room.setBranch(branch);
        room.setBranchCode(branch.getBranchCode());
        room.setRoomNumber(request.getRoomNumber());
        room.setArea(request.getArea());
        room.setPrice(request.getPrice());
        room.setStatus(request.getStatus());
        room.setDescription(request.getDescription());

        return RoomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(RoomMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
    }

    @Override
    public RoomResponse getRoomByCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode);
        if (room == null) throw new EntityNotFoundException("Room not found");
        return RoomMapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getRoomsByBranchCode(String branchCode) {
        return roomRepository.findByBranchCode(branchCode).stream()
                .map(RoomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status).stream()
                .map(RoomMapper::toResponse)
                .collect(Collectors.toList());
    }
}
