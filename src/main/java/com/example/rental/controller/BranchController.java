package com.example.rental.controller;

import com.example.rental.dto.branch.BranchRequest;
import com.example.rental.dto.branch.BranchResponse;
import com.example.rental.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Branch API", description = "Quản lý chi nhánh")
@SecurityRequirement(name = "Bearer Authentication") // ✅ để Swagger gửi JWT
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // nếu muốn USER cũng xem thì bỏ MANAGER
    @Operation(summary = "Lấy danh sách tất cả chi nhánh")
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy chi nhánh theo ID")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @GetMapping("/code/{branchCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Lấy chi nhánh theo mã code")
    public ResponseEntity<BranchResponse> getBranchByCode(@PathVariable String branchCode) {
        return ResponseEntity.ok(branchService.getBranchByCode(branchCode));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // ✅ chỉ ADMIN tạo được
    @Operation(summary = "Tạo chi nhánh mới")
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.createBranch(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật chi nhánh theo ID")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequest request
    ) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa chi nhánh theo ID")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}
