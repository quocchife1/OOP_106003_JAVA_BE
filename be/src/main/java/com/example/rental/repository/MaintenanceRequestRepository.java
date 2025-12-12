package com.example.rental.repository;

import com.example.rental.entity.MaintenanceRequest;
import com.example.rental.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByTenantId(Long tenantId);
    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);
}
