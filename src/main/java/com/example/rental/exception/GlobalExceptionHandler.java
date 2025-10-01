package com.example.rental.exception;

import com.example.rental.dto.ApiResponseDto;

import io.jsonwebtoken.ExpiredJwtException;	

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
    public ResponseEntity<ApiResponseDto<Void>> handleBadRequest(BadRequestException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
            HttpStatus.BAD_REQUEST.value(),
            "Yêu cầu không hợp lệ",
            ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex){
    	ApiResponseDto<Void> response = ApiResponseDto.error(
    			HttpStatus.UNAUTHORIZED.value(),
    			"Thông tin đăng nhập không hợp lệ",
    			ex.getMessage()
    	);
    	return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException  ex){
    	ApiResponseDto<Void> response = ApiResponseDto.error(
    			HttpStatus.FORBIDDEN.value(),
    			"Không có quyền truy cập",
    			ex.getMessage()
    	);
    	return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFoundException(BadCredentialsException ex){
    	ApiResponseDto<Void> response = ApiResponseDto.error(
    			HttpStatus.UNAUTHORIZED.value(),
    			"Thông tin đăng nhập không hợp lệ",
    			ex.getMessage()
    	);
    	return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleOtherExceptions(Exception ex) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Lỗi hệ thống",
            ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleExpiredJwtException(ExpiredJwtException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.error(
            HttpStatus.UNAUTHORIZED.value(),
            "JWT token hết hạn",
            ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        ApiResponseDto<Void> response = ApiResponseDto.error(
            HttpStatus.BAD_REQUEST.value(),
            "Dữ liệu không hợp lệ",
            errorMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
