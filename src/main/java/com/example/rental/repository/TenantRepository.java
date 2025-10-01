package com.example.rental.repository;

import com.example.rental.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Tìm người thuê theo mã người thuê
    Optional<Tenant> findByTenantCode(String tenantCode);

    // Tìm người thuê theo CCCD
    Optional<Tenant> findByCccd(String cccd);
}