package com.example.rental.exception;

import com.example.rental.dto.ApiResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                "Yêu cầu không hợp lệ",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.UNAUTHORIZED.value(),
                "Thông tin đăng nhập không hợp lệ",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.FORBIDDEN.value(),
                "Không có quyền truy cập",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.UNAUTHORIZED.value(),
                "Thông tin đăng nhập không hợp lệ",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT token hết hạn",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.BAD_REQUEST.value(),
                "Dữ liệu không hợp lệ",
                errorMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Lỗi hệ thống",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
