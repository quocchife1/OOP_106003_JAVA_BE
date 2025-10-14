package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.dto.partner.PartnerUpdateProfileRequest;
import com.example.rental.entity.UserStatus;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.mapper.PartnerMapper;
import com.example.rental.service.PartnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/management/partners")
@RequiredArgsConstructor
@Tag(name = "Partner Management")
public class PartnerController {

    private final PartnerService partnerService;
    private final PartnerMapper partnerMapper;

    /**
     * Lấy danh sách tất cả đối tác
     */
    @Operation(
        summary = "Lấy danh sách đối tác",
        description = "Trả về danh sách tất cả các đối tác hiện có trong hệ thống, bao gồm thông tin cơ bản như tên, email và trạng thái tài khoản."
    )
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<PartnerResponse>>> getAllPartners() {
        List<PartnerResponse> responses = partnerService.findAllPartners().stream()
                .map(partnerMapper::toResponse)
                .toList();
                
        return ResponseEntity.ok(ApiResponseDto.success(
                HttpStatus.OK.value(), 
                "Danh sách đối tác", 
                responses)
        );
    }

    /**
     * Lấy thông tin chi tiết của một đối tác theo ID
     */
    @Operation(
        summary = "Lấy chi tiết đối tác theo ID",
        description = "Truy vấn thông tin chi tiết của đối tác dựa vào ID. Nếu ID không tồn tại, hệ thống sẽ trả về lỗi `ResourceNotFoundException`."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PartnerResponse>> getPartnerById(@PathVariable Long id) {
        PartnerResponse response = partnerService.findById(id)
                .map(partnerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));

        return ResponseEntity.ok(ApiResponseDto.success(
                HttpStatus.OK.value(), 
                "Chi tiết đối tác", 
                response)
        );
    }

    /**
     * Chuyển đổi trạng thái tài khoản (kích hoạt / khóa)
     */
    @Operation(
        summary = "Chuyển đổi trạng thái tài khoản đối tác",
        description = "Thay đổi trạng thái của đối tác giữa `ACTIVE` và `INACTIVE`. Dùng để khóa hoặc mở khóa tài khoản đối tác."
    )
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponseDto<PartnerResponse>> togglePartnerStatus(@PathVariable Long id) {
        PartnerResponse response = partnerMapper.toResponse(partnerService.toggleStatus(id));

        String message = response.getStatus() == UserStatus.ACTIVE 
                         ? "Đã kích hoạt tài khoản đối tác" 
                         : "Đã khóa tài khoản đối tác";

        return ResponseEntity.ok(ApiResponseDto.success(
                HttpStatus.OK.value(), 
                message, 
                response)
        );
    }
    
    /**
     * Cập nhật thông tin hồ sơ đối tác theo ID.
     */
    @Operation(
        summary = "Cập nhật hồ sơ đối tác",
        description = "Cập nhật các thông tin hồ sơ của đối tác theo ID, bao gồm tên, địa chỉ, số điện thoại,... Dữ liệu đầu vào cần hợp lệ theo `PartnerUpdateProfileRequest`."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PartnerResponse>> updatePartnerProfile(
            @PathVariable Long id, 
            @Valid @RequestBody PartnerUpdateProfileRequest request) {
        
        PartnerResponse response = partnerService.updatePartnerProfile(id, request);

        return ResponseEntity.ok(ApiResponseDto.success(
                HttpStatus.OK.value(), 
                "Cập nhật hồ sơ đối tác thành công", 
                response)
        );
    }
}