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
import org.springframework.http.*;
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

    @Operation(summary = "Tải hợp đồng PDF gốc")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadContract(@PathVariable Long id) throws IOException {
        Resource resource = contractService.downloadContract(id);
        Path filePath = Path.of(resource.getFile().getAbsolutePath());
        String contentType = Files.probeContentType(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }
}
