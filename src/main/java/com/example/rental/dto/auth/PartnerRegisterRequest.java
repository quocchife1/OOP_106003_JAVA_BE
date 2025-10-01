package com.example.rental.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PartnerRegisterRequest extends AuthRegisterRequest {
    
    @Schema(description = "Tên công ty/chủ trọ", example = "Công ty TNHH Dịch vụ Phòng trọ X")
    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

    @Schema(description = "Mã số thuế", example = "0312345678")
    private String taxCode;

    @Schema(description = "Người liên hệ", example = "Nguyễn Văn A")
    @NotBlank(message = "Người liên hệ không được để trống")
    private String contactPerson;

    @Schema(description = "Địa chỉ đăng ký kinh doanh/nơi ở", example = "123 Đường Ba Tháng Hai, Quận 10")
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}