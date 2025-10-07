package com.example.rental.controller;

import com.example.rental.dto.rentalservice.RentalServiceRequest;
import com.example.rental.dto.rentalservice.RentalServiceResponse;
import com.example.rental.service.RentalServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Rental Service API", description = "Quản lý các dịch vụ nhà trọ")
@SecurityRequirement(name = "Bearer Authentication")
public class RentalServiceController {

    private final RentalServiceService rentalServiceService;

    @Operation(summary = "Tạo dịch vụ mới")
    @PostMapping
    public ResponseEntity<RentalServiceResponse> create(@RequestBody RentalServiceRequest request) {
        return ResponseEntity.ok(rentalServiceService.create(request));
    }

    @Operation(summary = "Cập nhật dịch vụ theo ID")
    @PutMapping("/{id}")
    public ResponseEntity<RentalServiceResponse> update(
            @PathVariable Long id,
            @RequestBody RentalServiceRequest request
    ) {
        return ResponseEntity.ok(rentalServiceService.update(id, request));
    }

    @Operation(summary = "Xoá dịch vụ theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lấy dịch vụ theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<RentalServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalServiceService.getById(id));
    }

    @Operation(summary = "Lấy toàn bộ dịch vụ")
    @GetMapping
    public ResponseEntity<List<RentalServiceResponse>> getAll() {
        return ResponseEntity.ok(rentalServiceService.getAll());
    }
}
