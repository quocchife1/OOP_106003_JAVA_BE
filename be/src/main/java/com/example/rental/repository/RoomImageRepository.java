package com.example.rental.repository;

import com.example.rental.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    // Tìm ảnh theo ID phòng
    List<RoomImage> findByRoomId(Long roomId);

    // Tìm ảnh thumbnail của phòng
    RoomImage findByRoomIdAndIsThumbnailTrue(Long roomId);
}