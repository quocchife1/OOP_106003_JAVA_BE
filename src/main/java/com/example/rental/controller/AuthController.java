package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.auth.AuthLoginRequest;
import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.auth.AuthResponse;
import com.example.rental.dto.auth.EmployeeRegisterRequest; // NEW IMPORT
import com.example.rental.dto.auth.PartnerRegisterRequest; // NEW IMPORT
import com.example.rental.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/guest")
    public ResponseEntity<ApiResponseDto<Void>> registerGuest(@Valid @RequestBody AuthRegisterRequest request) {
        authService.registerGuest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Guest registered successfully"));
    }
    
    @PostMapping("/register/tenant")
    public ResponseEntity<ApiResponseDto<Void>> registerTenant(@Valid @RequestBody AuthRegisterRequest request) {
        authService.registerTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Tenant registered successfully"));
    }

    // NEW ENDPOINT: Đăng ký Đối tác
    @PostMapping("/register/partner")
    public ResponseEntity<ApiResponseDto<Void>> registerPartner(@Valid @RequestBody PartnerRegisterRequest request) {
        authService.registerPartner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Partner registered successfully"));
    }

    // NEW ENDPOINT: Đăng ký Nhân viên (Dùng cho Admin tạo tài khoản nhân viên mới)
    @PostMapping("/register/employee")
    public ResponseEntity<ApiResponseDto<Void>> registerEmployee(@Valid @RequestBody EmployeeRegisterRequest request) {
        authService.registerEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Employee registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Login successful", authResponse));
    }
}