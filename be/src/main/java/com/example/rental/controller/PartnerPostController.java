package com.example.rental.controller;

import com.example.rental.dto.ApiResponseDto;
import com.example.rental.dto.partnerpost.PartnerPostCreateRequest;
import com.example.rental.dto.partnerpost.PartnerPostResponse;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.entity.PostImage;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.PartnerRepository;
import com.example.rental.repository.PostImageRepository;
import com.example.rental.service.PartnerPostService;
import com.example.rental.utils.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/partner-posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Partner Posts", description = "API quản lý tin đăng của đối tác")
public class PartnerPostController {

    private final PartnerPostService partnerPostService;
    private final PartnerRepository partnerRepository;
    private final PostImageRepository postImageRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    /**
     * Lấy danh sách tin đăng của partner hiện tại (đang đăng nhập)
     */
    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponseDto<List<PartnerPostResponse>>> getMyPosts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Partners partner = partnerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));

        List<PartnerPost> posts = partnerPostService.findPostsByPartnerId(partner.getId());
        List<PartnerPostResponse> responses = posts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponseDto.success(200, "Lấy danh sách tin đăng thành công", responses));
    }

        /**
         * Lấy danh sách tin đăng của partner hiện tại (có phân trang)
         */
        @GetMapping("/my-posts/paged")
        @PreAuthorize("hasRole('PARTNER')")
        public ResponseEntity<ApiResponseDto<Page<PartnerPostResponse>>> getMyPostsPaged(Pageable pageable) {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                Partners partner = partnerRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));

                Page<PartnerPost> page = partnerPostService.findPostsByPartnerId(partner.getId(), pageable);
                Page<PartnerPostResponse> response = page.map(this::mapToResponse);
                return ResponseEntity.ok(ApiResponseDto.success(200, "Lấy danh sách tin đăng (phân trang) thành công", response));
        }

    /**
     * Tạo tin đăng mới với ảnh (tối đa 5 ảnh)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponseDto<PartnerPostResponse>> createPost(
            @RequestPart("data") String requestData,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        
        try {
            // Parse JSON request data
            PartnerPostCreateRequest request = objectMapper.readValue(requestData, PartnerPostCreateRequest.class);
            
            // Validate images count (max 5)
            if (images != null && images.length > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error(400, "Chỉ được tải lên tối đa 5 ảnh", null));
            }
            
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Partners partner = partnerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));

            PartnerPost post = PartnerPost.builder()
                    .partner(partner)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .area(request.getArea())
                    .address(request.getAddress())
                    .postType(request.getPostType())
                    .build();

            PartnerPost savedPost = partnerPostService.createPost(post);
            
            // Save images if provided
            if (images != null && images.length > 0) {
                for (int i = 0; i < images.length; i++) {
                    MultipartFile image = images[i];
                    if (!image.isEmpty()) {
                        String filename = fileStorageService.storeFile(image, "partner-posts");
                        PostImage postImage = PostImage.builder()
                                .post(savedPost)
                                .imageUrl("/uploads/partner-posts/" + filename)
                                .isThumbnail(i == 0) // First image is thumbnail
                                .build();
                        postImageRepository.save(postImage);
                    }
                }
            }
            
            PartnerPostResponse response = mapToResponse(savedPost);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(201, "Tạo tin đăng thành công. Tin đăng đang chờ duyệt.", response));
        } catch (Exception e) {
            log.error("Error creating post with images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error(500, "Lỗi khi tạo tin đăng: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy thông tin chi tiết một tin đăng
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDto<PartnerPostResponse>> getPostById(@PathVariable Long id) {
        PartnerPost post = partnerPostService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartnerPost", "id", id));

        // Verify ownership if PARTNER role
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARTNER"))) {
            Partners partner = partnerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));
            if (!post.getPartner().getId().equals(partner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.error(403, "Bạn không có quyền xem tin đăng này", null));
            }
        }

        PartnerPostResponse response = mapToResponse(post);
        return ResponseEntity.ok(ApiResponseDto.success(200, "Lấy thông tin tin đăng thành công", response));
    }

    /**
     * Cập nhật tin đăng (chỉ khi PENDING hoặc REJECTED) với ảnh mới
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponseDto<PartnerPostResponse>> updatePost(
            @PathVariable Long id,
            @RequestPart("data") String requestData,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        
        try {
            PartnerPostCreateRequest request = objectMapper.readValue(requestData, PartnerPostCreateRequest.class);
            
            // Validate images count (max 5)
            if (images != null && images.length > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error(400, "Chỉ được tải lên tối đa 5 ảnh", null));
            }
            
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Partners partner = partnerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));

            PartnerPost post = partnerPostService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("PartnerPost", "id", id));

            // Verify ownership
            if (!post.getPartner().getId().equals(partner.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.error(403, "Bạn không có quyền sửa tin đăng này", null));
            }

            // Only allow edit if PENDING or REJECTED
            if (post.getStatus() == PostApprovalStatus.APPROVED) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error(400, "Không thể sửa tin đã được duyệt", null));
            }

            post.setTitle(request.getTitle());
            post.setDescription(request.getDescription());
            post.setPrice(request.getPrice());
            post.setArea(request.getArea());
            post.setAddress(request.getAddress());
            post.setPostType(request.getPostType());

            PartnerPost updated = partnerPostService.updatePost(post);
            
            // Update images if provided
            if (images != null && images.length > 0) {
                // Delete old images
                List<PostImage> oldImages = postImageRepository.findByPostId(id);
                postImageRepository.deleteAll(oldImages);
                
                // Save new images
                for (int i = 0; i < images.length; i++) {
                    MultipartFile image = images[i];
                    if (!image.isEmpty()) {
                        String filename = fileStorageService.storeFile(image, "partner-posts");
                        PostImage postImage = PostImage.builder()
                                .post(updated)
                                .imageUrl("/uploads/partner-posts/" + filename)
                                .isThumbnail(i == 0)
                                .build();
                        postImageRepository.save(postImage);
                    }
                }
            }
            
            PartnerPostResponse response = mapToResponse(updated);
            return ResponseEntity.ok(ApiResponseDto.success(200, "Cập nhật tin đăng thành công", response));
        } catch (Exception e) {
            log.error("Error updating post with images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error(500, "Lỗi khi cập nhật tin đăng: " + e.getMessage(), null));
        }
    }

    /**
     * Xóa tin đăng (chỉ khi PENDING hoặc REJECTED)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<ApiResponseDto<Void>> deletePost(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Partners partner = partnerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "username", username));

        PartnerPost post = partnerPostService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartnerPost", "id", id));

        // Verify ownership
        if (!post.getPartner().getId().equals(partner.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseDto.error(403, "Bạn không có quyền xóa tin đăng này", null));
        }

        // Only allow delete if PENDING or REJECTED
        if (post.getStatus() == PostApprovalStatus.APPROVED) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error(400, "Không thể xóa tin đã được duyệt. Vui lòng liên hệ quản trị viên.", null));
        }

        partnerPostService.deletePost(id);

        return ResponseEntity.ok(ApiResponseDto.success(200, "Xóa tin đăng thành công", null));
    }

    // Helper method to map entity to response DTO
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
}
