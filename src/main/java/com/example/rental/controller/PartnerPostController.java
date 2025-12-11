package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.post.PartnerPostRequest;
import com.example.rental.dto.post.PartnerPostResponse;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.service.PartnerPostService;
import com.example.rental.service.PartnerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partner-posts")
@RequiredArgsConstructor
@Tag(name = "Partner Posts", description = "Quản lý bài đăng của đối tác")
public class PartnerPostController {

        private final PartnerPostService partnerPostService;
        private final PartnerService partnerService;

        // =========================
        // Tạo bài đăng mới
        // =========================

        @PostMapping("/create")
        public ResponseEntity<ApiResponseDto<String>> createPartnerPost(
                        @RequestBody PartnerPostRequest postRequest) {
                String paymentUrl = partnerPostService.createPost(postRequest);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponseDto.success(201,
                                                "Bài đăng đã được lưu, vui lòng thanh toán để hoàn tất", paymentUrl));
        }

        // =========================
        // Lấy danh sách bài đăng theo Partner ID
        // =========================
        @GetMapping("/partner/{partnerId}")
        public ResponseEntity<ApiResponseDto<List<PartnerPostResponse>>> getPostsByPartnerId(
                        @PathVariable Long partnerId) {

                // Lấy thông tin đối tác
                Partners partner = partnerService.findById(partnerId)
                                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đối tác."));

                List<PartnerPostResponse> posts = partnerPostService.findPostsByPartnerId(partnerId);

                String message = "Danh sách bài đăng của đối tác " + partner.getUsername();

                return ResponseEntity.ok(
                                ApiResponseDto.success(200, message, posts));
        }

        // =========================
        // Lấy bài đăng theo ID
        // =========================
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponseDto<PartnerPostResponse>> getPostById(@PathVariable Long id) {
                PartnerPostResponse post = partnerPostService.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));
                return ResponseEntity.ok(
                                ApiResponseDto.success(200, "Chi tiết bài đăng", post));
        }

        // =========================
        // Lấy danh sách bài đăng theo trạng thái duyệt
        // =========================
        @GetMapping("/status/{status}")
        public ResponseEntity<ApiResponseDto<List<PartnerPostResponse>>> getPostsByStatus(
                        @PathVariable PostApprovalStatus status) {
                List<PartnerPostResponse> posts = partnerPostService.findPostsByStatus(status);
                return ResponseEntity.ok(
                                ApiResponseDto.success(200, "Danh sách bài đăng theo trạng thái",
                                                posts));
        }

        // =========================
        // Nhân viên duyệt bài
        // =========================
        @PatchMapping("/{postId}/approve")
        public ResponseEntity<ApiResponseDto<PartnerPost>> approvePost(
                        @PathVariable Long postId,
                        @RequestParam Long employeeId,
                        @RequestParam PostApprovalStatus status) {
                PartnerPost approved = partnerPostService.approvePost(postId, employeeId, status);
                return ResponseEntity.ok(
                                ApiResponseDto.success(HttpStatus.OK.value(), "Cập nhật trạng thái bài đăng thành công",
                                                null));
        }
}
