package com.example.rental.service;

import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.tenant.TenantResponse;
import com.example.rental.dto.tenant.TenantUpdateProfileRequest;
import com.example.rental.entity.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    TenantResponse registerNewTenant(AuthRegisterRequest request); 
    TenantResponse updateTenantProfile(Long id, TenantUpdateProfileRequest request);

    List<Tenant> findAllTenants();
    Optional<Tenant> findById(Long id);
    Optional<Tenant> findByUsername(String username);
    boolean isUsernameExists(String username);
    boolean isEmailExists(String email);
}