package com.example.rental.service.impl;

import com.example.rental.entity.MaintenanceRequest;
import com.example.rental.entity.MaintenanceStatus;
import com.example.rental.repository.MaintenanceRequestRepository;
import com.example.rental.service.MaintenanceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    @Override
    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        // Logic nghiệp vụ: Có thể tạo requestCode tự động tại đây
        request.setStatus(MaintenanceStatus.PENDING);
        return maintenanceRequestRepository.save(request);
    }

    @Override
    public Optional<MaintenanceRequest> findById(Long id) {
        return maintenanceRequestRepository.findById(id);
    }

    @Override
    public List<MaintenanceRequest> findRequestsByTenantId(Long tenantId) {
        return maintenanceRequestRepository.findByTenantId(tenantId);
    }

    @Override
    public List<MaintenanceRequest> findRequestsByStatus(MaintenanceStatus status) {
        return maintenanceRequestRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public MaintenanceRequest updateStatus(Long requestId, MaintenanceStatus newStatus) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu bảo trì."));
        
        // Logic nghiệp vụ: Đảm bảo chuyển trạng thái hợp lệ
        // (Ví dụ: Không thể chuyển từ COMPLETED sang PENDING)

        request.setStatus(newStatus);
        return maintenanceRequestRepository.save(request);
    }
}