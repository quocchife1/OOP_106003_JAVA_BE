package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.partnerpost.PartnerPostResponse;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostImage;
import com.example.rental.repository.PartnerPostRepository;
import com.example.rental.repository.PostImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/management/partner-posts")
public class ManagementPartnerPostController {

    private final PartnerPostRepository partnerPostRepository;
    private final PostImageRepository postImageRepository;

    public ManagementPartnerPostController(PartnerPostRepository partnerPostRepository,
                                           PostImageRepository postImageRepository) {
        this.partnerPostRepository = partnerPostRepository;
        this.postImageRepository = postImageRepository;
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Page<PartnerPostResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String q,
            Pageable pageable
    ) {
        java.util.List<com.example.rental.entity.PostApprovalStatus> statuses;
        if (status == null || status.isBlank()) {
            statuses = java.util.Arrays.asList(
                    com.example.rental.entity.PostApprovalStatus.PENDING_APPROVAL,
                    com.example.rental.entity.PostApprovalStatus.APPROVED,
                    com.example.rental.entity.PostApprovalStatus.REJECTED
            );
        } else {
            // Accept PENDING/PENDING_APPROVAL, APPROVED, REJECTED
            String norm = status.trim().toUpperCase();
            if ("PENDING".equals(norm)) norm = "PENDING_APPROVAL";
            com.example.rental.entity.PostApprovalStatus st = com.example.rental.entity.PostApprovalStatus.valueOf(norm);
            statuses = java.util.List.of(st);
        }

        String keyword = q == null ? "" : q.trim();
        Page<PartnerPost> page = partnerPostRepository
                .findByStatusInAndTitleContainingIgnoreCaseAndIsDeletedFalse(statuses, keyword, pageable);
        Page<PartnerPostResponse> resp = page.map(this::mapToResponse);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Danh sách tin theo bộ lọc", resp));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Page<PartnerPostResponse>>> listPending(Pageable pageable) {
        Page<PartnerPost> page = partnerPostRepository.findByStatusInAndIsDeletedFalse(java.util.List.of(com.example.rental.entity.PostApprovalStatus.PENDING_APPROVAL), pageable);
        Page<PartnerPostResponse> resp = page.map(this::mapToResponse);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Danh sách tin chờ duyệt", resp));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<PartnerPostResponse>> getById(@PathVariable Long id) {
        PartnerPost post = partnerPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tin"));
        return ResponseEntity.ok(ApiResponseDto.success(200, "Lấy chi tiết tin thành công", mapToResponse(post)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Void>> approve(@PathVariable Long id) {
        PartnerPost post = partnerPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tin"));
        post.setStatus(com.example.rental.entity.PostApprovalStatus.APPROVED);
        post.setApprovedAt(java.time.LocalDateTime.now());
        partnerPostRepository.save(post);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Đã duyệt tin", null));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Void>> reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        PartnerPost post = partnerPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tin"));
        post.setStatus(com.example.rental.entity.PostApprovalStatus.REJECTED);
        post.setRejectReason(reason);
        partnerPostRepository.save(post);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Đã từ chối tin", null));
    }

    private PartnerPostResponse mapToResponse(PartnerPost post) {
        List<String> imageUrls = postImageRepository.findByPostId(post.getId())
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        return PartnerPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .area(post.getArea())
                .address(post.getAddress())
                .postType(post.getPostType())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .approvedAt(post.getApprovedAt())
                .approvedByName(post.getApprovedBy() != null ? post.getApprovedBy().getFullName() : null)
                .partnerId(post.getPartner().getId())
                .partnerName(post.getPartner().getCompanyName())
                .partnerPhone(post.getPartner().getPhoneNumber())
                .rejectReason(post.getRejectReason())
                .imageUrls(imageUrls)
                .build();
    }

    // ===== Bulk operations =====
    public static class IdsRequest {
        public java.util.List<Long> ids;
        public String reason; // optional for reject-batch
        public java.util.List<Long> getIds() { return ids; }
        public String getReason() { return reason; }
    }

    @PostMapping("/approve-batch")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> approveBatch(@RequestBody IdsRequest req) {
        List<Long> ids = req != null && req.ids != null ? req.ids : java.util.List.of();
        int updated = 0;
        if (!ids.isEmpty()) {
            List<PartnerPost> posts = partnerPostRepository.findAllById(ids);
            LocalDateTime now = java.time.LocalDateTime.now();
            for (PartnerPost p : posts) {
                p.setStatus(com.example.rental.entity.PostApprovalStatus.APPROVED);
                p.setApprovedAt(now);
                updated++;
            }
            partnerPostRepository.saveAll(posts);
        }
        Map<String, Object> result = java.util.Map.of("updated", updated, "requested", ids.size());
        return ResponseEntity.ok(ApiResponseDto.success(200, "Đã duyệt hàng loạt", result));
    }

    @PostMapping("/reject-batch")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> rejectBatch(@RequestBody IdsRequest req) {
        List<Long> ids = req != null && req.ids != null ? req.ids : java.util.List.of();
        String reason = req != null ? req.reason : null;
        int updated = 0;
        if (!ids.isEmpty()) {
            List<PartnerPost> posts = partnerPostRepository.findAllById(ids);
            for (PartnerPost p : posts) {
                p.setStatus(com.example.rental.entity.PostApprovalStatus.REJECTED);
                p.setRejectReason(reason);
                updated++;
            }
            partnerPostRepository.saveAll(posts);
        }
        Map<String, Object> result = java.util.Map.of("updated", updated, "requested", ids.size());
        return ResponseEntity.ok(ApiResponseDto.success(200, "Đã từ chối hàng loạt", result));
    }

    // ===== Stats =====
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','RECEPTIONIST')")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> stats() {
        long pending = partnerPostRepository.countByStatusAndIsDeletedFalse(com.example.rental.entity.PostApprovalStatus.PENDING_APPROVAL);
        long approved = partnerPostRepository.countByStatusAndIsDeletedFalse(com.example.rental.entity.PostApprovalStatus.APPROVED);
        long rejected = partnerPostRepository.countByStatusAndIsDeletedFalse(com.example.rental.entity.PostApprovalStatus.REJECTED);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long approvedToday = partnerPostRepository.countByApprovedAtBetweenAndIsDeletedFalse(start, end);

        Map<String, Object> result = java.util.Map.of(
                "pending", pending,
                "approved", approved,
                "rejected", rejected,
                "approvedToday", approvedToday
        );
        return ResponseEntity.ok(ApiResponseDto.success(200, "Thống kê duyệt tin", result));
    }
}
