package com.example.rental.mapper;

import com.example.rental.entity.MaintenanceRequest;
import com.example.rental.dto.maintenance.*;
import org.springframework.stereotype.Component; // <-- Import quan trọng

import java.util.stream.Collectors;

@Component // <-- Thêm annotation này
public class MaintenanceMapper {
    
    // Bỏ từ khóa 'static'
    public MaintenanceResponse toResponse(MaintenanceRequest entity) {
        return MaintenanceResponse.builder()
                .id(entity.getId())
                .requestCode(entity.getRequestCode())
                .tenantName(entity.getTenant() != null ? entity.getTenant().getFullName() : "N/A") // Null check an toàn
                .roomNumber(entity.getRoom() != null ? entity.getRoom().getRoomNumber() : "N/A")
                .description(entity.getDescription())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .resolution(entity.getResolution())
                .cost(entity.getCost())
                .technicianName(entity.getTechnicianName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .images(entity.getImages() != null ?
                        entity.getImages().stream().map(i -> i.getImageUrl()).collect(Collectors.toList())
                        : null)
                .build();
    }
}