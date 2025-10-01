package com.example.rental.service.impl;

import com.example.rental.dto.auth.AuthLoginRequest;
import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.auth.AuthResponse;
import com.example.rental.dto.auth.EmployeeRegisterRequest;
import com.example.rental.dto.auth.PartnerRegisterRequest;
import com.example.rental.entity.Guest;
import com.example.rental.entity.UserStatus;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.repository.GuestRepository;
import com.example.rental.repository.PartnerRepository;
import com.example.rental.repository.TenantRepository;
import com.example.rental.security.JwtProvider;
import com.example.rental.service.AuthService;
import com.example.rental.service.EmployeeService; 
import com.example.rental.service.TenantService; 
import com.example.rental.service.PartnerService; 
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final GuestRepository guestRepository;
    private final TenantRepository tenantRepository;
    private final PartnerRepository partnerRepository;
    private final EmployeeRepository employeeRepository;
    
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    
    // Inject các Service chuyên biệt để ủy quyền đăng ký
    private final EmployeeService employeeService;
    private final TenantService tenantService; 
    private final PartnerService partnerService; 
    

    private void checkUserExistence(String username, String email) {
        if (guestRepository.existsByUsername(username) ||
            tenantRepository.existsByUsername(username) ||
            partnerRepository.existsByUsername(username) ||
            employeeRepository.existsByUsername(username)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại trong hệ thống.");
        }
        if (guestRepository.existsByEmail(email) ||
            tenantRepository.existsByEmail(email) ||
            partnerRepository.existsByEmail(email) ||
            employeeRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống.");
        }
    }

    @Override
    @Transactional
    public void registerGuest(AuthRegisterRequest request) {
        checkUserExistence(request.getUsername(), request.getEmail());

        // Tạm thời giữ lại logic Entity để đảm bảo biên dịch (nên chuyển sang GuestService)
        Guest newGuest = Guest.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .fullName(request.getFullName())
                .status(UserStatus.ACTIVE)
                .build();
        guestRepository.save(newGuest);
    }

    @Override
    @Transactional
    public void registerTenant(AuthRegisterRequest request) {
        checkUserExistence(request.getUsername(), request.getEmail());
        
        // Ủy quyền hoàn toàn cho TenantService
        tenantService.registerNewTenant(request); 
    }
    
    @Override
    @Transactional
    public void registerPartner(PartnerRegisterRequest request) { 
        checkUserExistence(request.getUsername(), request.getEmail());
        
        // Ủy quyền hoàn toàn cho PartnerService
        partnerService.registerNewPartner(request);
    }
    
    @Override
    @Transactional
    public void registerEmployee(EmployeeRegisterRequest request) { 
        checkUserExistence(request.getUsername(), request.getEmail());
        
        // Ủy quyền hoàn toàn cho EmployeeService
        employeeService.registerNewEmployee(request);
    }
    
    @Override
    public AuthResponse login(AuthLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateAccessToken(authentication);
        return AuthResponse.builder().accessToken(token).build();
    }
}