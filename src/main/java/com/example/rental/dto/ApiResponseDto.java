package com.example.rental.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {

    @Schema(description = "Mã trạng thái HTTP hoặc code tùy ý", example = "200")
    private int statusCode;

    @Schema(description = "Thông điệp phản hồi", example = "Thành công")
    private String message;

    @Schema(description = "Chi tiết lỗi nếu có", example = "Dữ liệu không hợp lệ")
    private String error;

    @Schema(description = "Dữ liệu trả về nếu có, phụ thuộc vào API")
    private T data;

    public static <T> ApiResponseDto<T> success(int statusCode, String message) {
        return ApiResponseDto.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .build();
    }

    public static <T> ApiResponseDto<T> success(int statusCode, String message, T data) {
        return ApiResponseDto.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> error(int statusCode, String message, String error) {
        return ApiResponseDto.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .build();
    }
}