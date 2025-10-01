package com.example.rental.service;

import com.example.rental.entity.MaintenanceRequest;
import com.example.rental.entity.MaintenanceStatus;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestService {
    // Tạo yêu cầu bảo trì mới
    MaintenanceRequest createRequest(MaintenanceRequest request);

    // Lấy yêu cầu theo ID
    Optional<MaintenanceRequest> findById(Long id);

    // Lấy yêu cầu theo ID người thuê
    List<MaintenanceRequest> findRequestsByTenantId(Long tenantId);

    // Lấy yêu cầu theo trạng thái (Dành cho nhân viên)
    List<MaintenanceRequest> findRequestsByStatus(MaintenanceStatus status);

    // Cập nhật trạng thái yêu cầu
    MaintenanceRequest updateStatus(Long requestId, MaintenanceStatus newStatus);
}