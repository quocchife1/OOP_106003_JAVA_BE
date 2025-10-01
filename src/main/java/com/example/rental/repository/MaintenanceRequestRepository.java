package com.example.rental.repository;

import com.example.rental.entity.MaintenanceRequest;
import com.example.rental.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    // Tìm yêu cầu theo ID người thuê
    List<MaintenanceRequest> findByTenantId(Long tenantId);

    // Tìm yêu cầu theo trạng thái
    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);
}