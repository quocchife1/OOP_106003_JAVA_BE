package com.example.rental.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthRegisterRequest {
    @Schema(description = "Tài khoản người dùng", example = "quocchi5523")
    @NotBlank(message = "Tài khoản người dùng không được để trống")
    private String username;

    @Schema(description = "Mật khẩu người dùng", example = "Quocchi0523@")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$",
        message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    private String password;

    @Schema(description = "Email người dùng", example = "chinguyen123852@gmail.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Schema(description = "Số điện thoại người dùng", example = "0359444856")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
        regexp = "^(0|\\+84)(3[2-9]|5[25689]|7[0|6-9]|8[1-9]|9[0-9])\\d{7}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @Schema(description = "Họ và tên người dùng", example = "Nguyễn Quốc Chí")
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @Schema(description = "Ngày sinh người dùng", example = "2005-05-05")
    @NotBlank(message = "Ngày sinh không được để trống")
    private String dob;
}