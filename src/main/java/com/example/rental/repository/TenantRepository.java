package com.example.rental.repository;

import com.example.rental.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Tenant> findByCccd(String cccd);
    boolean existsByCccd(String cccd);
    boolean existsByStudentId(String studentId);
}
