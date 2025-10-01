package com.example.rental.dto.auth;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.rental.entity.EmployeePosition;
import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeRegisterRequest extends AuthRegisterRequest {

    @Schema(description = "Mã chi nhánh làm việc", example = "CN01")
    @NotNull(message = "Mã chi nhánh không được để trống")
    private String branchCode;

    @Schema(description = "Chức vụ", example = "RECEPTIONIST")
    @NotNull(message = "Chức vụ không được để trống")
    private EmployeePosition position;

    @Schema(description = "Lương nhân viên", example = "10000000")
    @NotNull(message = "Lương không được để trống")
    private BigDecimal salary;

    @Schema(description = "Ngày bắt đầu làm việc", example = "2024-02-01")
    @NotNull(message = "Ngày được thuê nhân viên không được để trống")
    private LocalDate hireDate;
}
