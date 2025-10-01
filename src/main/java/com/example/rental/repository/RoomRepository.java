package com.example.rental.repository;

import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByRoomCode(String roomCode);
    List<Room> findByBranchCode(String branchCode);
    List<Room> findByStatus(RoomStatus status);
}
