package com.example.rental.controller;

import com.example.rental.dto.room.RoomRequest;
import com.example.rental.dto.room.RoomResponse;
import com.example.rental.entity.RoomStatus;
import com.example.rental.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room API", description = "Quản lý phòng tại các chi nhánh")
@SecurityRequirement(name = "Bearer Authentication")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Tạo phòng mới", description = "Thêm mới một phòng vào hệ thống với branchCode và roomNumber")
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @Operation(summary = "Cập nhật phòng theo ID", description = "Chỉnh sửa thông tin phòng dựa trên ID")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id, @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @Operation(summary = "Xóa phòng theo ID", description = "Xóa vĩnh viễn phòng trong hệ thống")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy phòng theo ID", description = "Truy xuất thông tin phòng bằng ID")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @Operation(summary = "Lấy phòng theo roomCode", description = "Truy xuất phòng bằng mã roomCode (VD: CN01101)")
    @GetMapping("/code/{roomCode}")
    public ResponseEntity<RoomResponse> getRoomByCode(@PathVariable String roomCode) {
        return ResponseEntity.ok(roomService.getRoomByCode(roomCode));
    }

    @Operation(summary = "Lấy danh sách phòng theo branchCode", description = "Lấy tất cả phòng thuộc một chi nhánh thông qua branchCode (VD: CN01)")
    @GetMapping("/branch/{branchCode}")
    public ResponseEntity<List<RoomResponse>> getRoomsByBranchCode(@PathVariable String branchCode) {
        return ResponseEntity.ok(roomService.getRoomsByBranchCode(branchCode));
    }

    @Operation(summary = "Lấy phòng theo trạng thái", description = "Truy xuất danh sách phòng theo trạng thái (AVAILABLE, RENTED, MAINTENANCE)")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomResponse>> getRoomsByStatus(@PathVariable RoomStatus status) {
        return ResponseEntity.ok(roomService.getRoomsByStatus(status));
    }
}
