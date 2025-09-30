package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.auth.AuthLoginRequest;
import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.auth.AuthResponse;
import com.example.rental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "Đăng ký, đăng nhập người dùng")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản người dùng", description = "Tạo người dùng mới")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Đăng ký thành công", content = @Content),
            @ApiResponse(responseCode = "400", description = "Thông tin đăng ký đã tồn tại", content = @Content)
    })
    public ResponseEntity<ApiResponseDto<String>> register(@Valid @RequestBody AuthRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(201, "Đăng ký thành công"));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập người dùng", description = "Trả về JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Thông tin đăng nhập không hợp lệ", content = @Content)
    })
    public ResponseEntity<ApiResponseDto<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthResponse authRes = authService.login(request);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Đăng nhập thành công", authRes));
    }
}