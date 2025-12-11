package com.example.rental.service.impl;

import com.example.rental.dto.post.PartnerPostRequest;
import com.example.rental.dto.post.PartnerPostResponse;
import com.example.rental.entity.Employees;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;
import com.example.rental.entity.PostType;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.mapper.PartnerPostMapper;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.repository.PartnerPostRepository;
import com.example.rental.repository.PartnerRepository;
import com.example.rental.service.PartnerPostService;
import com.example.rental.service.MomoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerPostServiceImpl implements PartnerPostService {

    private final PartnerPostRepository partnerPostRepository;
    private final EmployeeRepository employeeRepository;
    private final PartnerRepository partnerRepository;
    private final MomoService momoService;

    @Override
    @Transactional
    public String createPost(PartnerPostRequest postRequest) {
        Partners partner = partnerRepository.findById(postRequest.getPartnerId())
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));

        String orderId = UUID.randomUUID().toString();

        PartnerPost partnerPost = PartnerPost.builder()
                .partner(partner)
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .price(postRequest.getPrice())
                .area(postRequest.getArea())
                .address(postRequest.getAddress())
                .postType(postRequest.getPostType())
                .status(PostApprovalStatus.PENDING_PAYMENT)
                .orderId(orderId)
                .build();

        PartnerPost saved = partnerPostRepository.save(partnerPost);

        long amount;
        if (postRequest.getPostType() == PostType.NORMAL)
            amount = 100000;
        else
            amount = 200000;

        String paymentUrl = momoService.createATMPayment(amount, orderId).getPayUrl();

        return paymentUrl;
    }

    @Override
    public Optional<PartnerPostResponse> findById(Long id) {
        return partnerPostRepository.findById(id)
            .map(PartnerPostMapper::toResponse);
    }

    @Override
    public List<PartnerPostResponse> findPostsByPartnerId(Long partnerId) {
        List<PartnerPost> posts = partnerPostRepository.findByPartnerId(partnerId);

        return posts.stream()
                .map(PartnerPostMapper::toResponse)
                .toList();
    }

    @Override
    public List<PartnerPostResponse> findPostsByStatus(PostApprovalStatus status) {
        List<PartnerPost> posts = partnerPostRepository.findByStatus(status);

        return posts.stream()
                .map(PartnerPostMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PartnerPost approvePost(Long postId, Long approvedByEmployeeId, PostApprovalStatus newStatus) {
        PartnerPost post = partnerPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin đăng."));

        if (post.getStatus() == PostApprovalStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Bài đăng đang chờ thanh toán, không thể duyệt.");
        }

        if (post.getStatus() == PostApprovalStatus.APPROVED || post.getStatus() == PostApprovalStatus.REJECTED) {
            throw new IllegalStateException("Bài đăng đã được duyệt, không thể sửa đổi trạng thái phê duyệt.");
        }

        // ĐÃ SỬA: So sánh với PostApprovalStatus.PENDING
        if (newStatus == PostApprovalStatus.PENDING_APPROVAL || newStatus == PostApprovalStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Không thể duyệt về trạng thái PENDING.");
        }

        Employees approver = employeeRepository.findById(approvedByEmployeeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên duyệt."));

        post.setStatus(newStatus);
        post.setApprovedBy(approver);
        post.setApprovedAt(LocalDateTime.now());

        return partnerPostRepository.save(post);
    }
}