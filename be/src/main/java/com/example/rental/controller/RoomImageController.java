package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.room.RoomImageResponse;
import com.example.rental.entity.Room;
import com.example.rental.entity.RoomImage;
import com.example.rental.repository.RoomImageRepository;
import com.example.rental.repository.RoomRepository;
import com.example.rental.utils.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomImageController {

    private final FileStorageService fileStorageService;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;

    @PostMapping(value = "/{roomId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<List<RoomImageResponse>>> uploadRoomImages(
            @PathVariable Long roomId,
            @RequestPart("images") MultipartFile[] images) throws IOException {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        List<RoomImageResponse> saved = new ArrayList<>();

        for (MultipartFile f : images) {
            // Lưu file vật lý
            String filename = fileStorageService.storeFile(f, "rooms");
            // Tạo đường dẫn URL (cần đảm bảo khớp với cấu hình ResourceHandler)
            String url = "/uploads/rooms/" + filename;

            RoomImage img = RoomImage.builder()
                    .room(room)
                    .imageUrl(url)
                    .isThumbnail(false)
                    .build();

            RoomImage persisted = roomImageRepository.save(img);
            saved.add(new RoomImageResponse(persisted.getId(), persisted.getImageUrl(), persisted.getIsThumbnail()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Uploaded room images", saved));
    }

    @GetMapping("/{roomId}/images")
    public ResponseEntity<ApiResponseDto<List<RoomImageResponse>>> listRoomImages(@PathVariable Long roomId) {
        List<RoomImage> imgs = roomImageRepository.findByRoomId(roomId);
        List<RoomImageResponse> resp = new ArrayList<>();
        
        for (RoomImage img : imgs) {
            resp.add(new RoomImageResponse(img.getId(), img.getImageUrl(), img.getIsThumbnail()));
        }
        
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Room images", resp));
    }
}