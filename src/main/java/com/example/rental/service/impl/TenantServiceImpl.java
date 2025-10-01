package com.example.rental.service.impl;

import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.tenant.TenantResponse;
import com.example.rental.dto.tenant.TenantUpdateProfileRequest;
import com.example.rental.entity.Tenant;
import com.example.rental.entity.UserStatus;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.mapper.TenantMapper;
import com.example.rental.repository.TenantRepository;
import com.example.rental.service.TenantService;
import com.example.rental.service.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final CodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;
    private final TenantMapper tenantMapper;

    @Override
    @Transactional
    public TenantResponse registerNewTenant(AuthRegisterRequest request) {
        // Giả định logic kiểm tra user đã được thực hiện ở AuthService
        Tenant newTenant = tenantMapper.registerRequestToTenant(request);
        
        newTenant.setPassword(passwordEncoder.encode(request.getPassword()));
        newTenant.setStatus(UserStatus.ACTIVE);
        
        Tenant savedTenant = tenantRepository.save(newTenant);
        
        String tenantCode = codeGenerator.generateCode("T", savedTenant.getId());
        savedTenant.setTenantCode(tenantCode); 

        Tenant finalTenant = tenantRepository.save(savedTenant);
        return tenantMapper.tenantToTenantResponse(finalTenant);
    }
    
    // ĐÃ XÓA: Phương thức registerNewTenant(Tenant tenant) cũ

    @Override
    @Transactional
    public TenantResponse updateTenantProfile(Long id, TenantUpdateProfileRequest request) {
        Tenant existingTenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
        
        // SỬ DỤNG MAPPER ĐỂ CẬP NHẬT
        tenantMapper.updateTenantFromDto(request, existingTenant);

        Tenant updatedTenant = tenantRepository.save(existingTenant);
        return tenantMapper.tenantToTenantResponse(updatedTenant);
    }
    
    // ĐÃ XÓA: Phương thức updateTenantProfile(Tenant updatedTenant) cũ

    @Override
    public List<Tenant> findAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public Optional<Tenant> findById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public Optional<Tenant> findByUsername(String username) {
        return tenantRepository.findByUsername(username);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return tenantRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return tenantRepository.existsByEmail(email);
    }
}