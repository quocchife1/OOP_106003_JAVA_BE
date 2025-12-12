package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.contract.ContractCreateRequest;
import com.example.rental.dto.contract.ContractResponse;
import com.example.rental.mapper.ContractMapper;
import com.example.rental.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Contract Management", description = "Quản lý hợp đồng thuê phòng")
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;
    private final com.example.rental.service.TenantService tenantService;
    private final com.example.rental.service.CheckoutService checkoutService;
    private final com.example.rental.service.InvoiceService invoiceService;

    @Operation(summary = "Tạo hợp đồng mới")
    @PostMapping
    public ResponseEntity<ApiResponseDto<ContractResponse>> createContract(
            @RequestBody ContractCreateRequest request) throws IOException {

        var contract = contractService.createContract(request);
        var response = contractMapper.toResponse(contract);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Tạo hợp đồng thành công", response));
    }

    @Operation(summary = "Upload hợp đồng đã ký (ảnh/pdf)")
    @PostMapping(value = "/{id}/upload-signed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<ContractResponse>> uploadSignedContract(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) throws IOException {

        var contract = contractService.uploadSignedContract(id, file);
        var response = contractMapper.toResponse(contract);

        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(),
                "Upload hợp đồng đã ký thành công", response));
    }

    @Operation(summary = "Lấy danh sách hợp đồng của người dùng hiện tại (Tenant)")
    @GetMapping("/my-contracts")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponseDto<java.util.List<ContractResponse>>> getMyContracts() {
        // Lấy username từ SecurityContext
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        var tenantOpt = tenantService.findByUsername(username);
        if (tenantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.success(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người thuê", null));
        }
        var tenant = tenantOpt.get();
        java.util.List<com.example.rental.entity.Contract> contracts = contractService.findByTenantId(tenant.getId());
        java.util.List<ContractResponse> responses = contracts.stream().map(contractMapper::toResponse).toList();
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Danh sách hợp đồng của bạn", responses));
    }

    @Operation(summary = "Lấy danh sách hợp đồng của người dùng hiện tại (Tenant) - phân trang")
    @GetMapping("/my-contracts/paged")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiResponseDto<Page<ContractResponse>>> getMyContractsPaged(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var tenantOpt = tenantService.findByUsername(username);
        if (tenantOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.success(HttpStatus.NOT_FOUND.value(), "Không tìm thấy người thuê", null));
        }
        var tenant = tenantOpt.get();
        Page<com.example.rental.entity.Contract> page = contractService.findByTenantId(tenant.getId(), pageable);
        Page<ContractResponse> responses = page.map(contractMapper::toResponse);
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Danh sách hợp đồng của bạn (phân trang)", responses));
    }

    @Operation(summary = "Người thuê gửi yêu cầu trả phòng (checkout request)")
    @PostMapping("/{id}/checkout-request")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT','GUEST')")
    public ResponseEntity<ApiResponseDto<com.example.rental.dto.checkout.CheckoutRequestResponse>> submitCheckoutRequest(
            @PathVariable Long id,
            @RequestBody com.example.rental.dto.checkout.CheckoutRequestDto request) {

        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        var resp = checkoutService.submitCheckoutRequest(id, username, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success(HttpStatus.CREATED.value(), "Yêu cầu trả phòng đã gửi", resp));
    }

    @Operation(summary = "Nhân viên duyệt yêu cầu trả phòng (approve)")
    @PutMapping("/checkout-requests/{requestId}/approve")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<com.example.rental.dto.checkout.CheckoutRequestResponse>> approveCheckoutRequest(
            @PathVariable Long requestId) {
        String approver = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        var resp = checkoutService.approveRequest(requestId, approver);
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Yêu cầu đã được duyệt", resp));
    }

    @Operation(summary = "Finalize checkout: đóng hợp đồng và tạo hóa đơn cuối")
    @PostMapping("/{id}/finalize-checkout")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Void>> finalizeCheckout(@PathVariable Long id) {
        String operator = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        checkoutService.finalizeCheckout(id, operator);
        return ResponseEntity.ok(ApiResponseDto.success(HttpStatus.OK.value(), "Hoàn tất trả phòng thành công"));
    }

    @Operation(summary = "Tải hợp đồng DOCX gốc")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long id) throws IOException {
        Resource resource = contractService.downloadContract(id);
        Path filePath = Path.of(resource.getFile().getAbsolutePath());
        String contentType = Files.probeContentType(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }
}
