package com.example.rental.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
        return new ApiResponseDto<>(statusCode, message, null, null);
    }

    public static <T> ApiResponseDto<T> success(int statusCode, String message, T data) {
        return new ApiResponseDto<>(statusCode, message, null, data);
    }

    public static <T> ApiResponseDto<T> error(int statusCode, String message, String error) {
        return new ApiResponseDto<>(statusCode, message, error, null);
    }
}