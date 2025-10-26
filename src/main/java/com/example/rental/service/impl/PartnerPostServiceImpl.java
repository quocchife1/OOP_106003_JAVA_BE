package com.example.rental.service.impl;

import com.example.rental.dto.post.PartnerPostRequest;
import com.example.rental.dto.post.PartnerPostResponse;
import com.example.rental.entity.Employees;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.mapper.PartnerPostMapper;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.repository.PartnerPostRepository;
import com.example.rental.repository.PartnerRepository;
import com.example.rental.service.PartnerPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerPostServiceImpl implements PartnerPostService {

    private final PartnerPostRepository partnerPostRepository;
    private final EmployeeRepository employeeRepository;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional
    public PartnerPostResponse createPost(PartnerPostRequest postRequest) {
        Partners partner = partnerRepository.findById(postRequest.getPartnerId())
            .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        
        PartnerPost partnerPost = PartnerPost.builder()
                .partner(partner)
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .price(postRequest.getPrice())
                .area(postRequest.getArea())
                .address(postRequest.getAddress())
                .postType(postRequest.getPostType())
                .status(PostApprovalStatus.PENDING_PAYMENT)
                .build();

        PartnerPost saved = partnerPostRepository.save(partnerPost);

        // 2. Tạo URL thanh toán (ví dụ giả lập)
        String paymentUrl = "https://momo.vn/pay?orderId=POST" + saved.getId();

        return new PartnerPostResponse(saved.getId(), paymentUrl);
    }

    @Override
    public Optional<PartnerPost> findById(Long id) {
        return partnerPostRepository.findById(id);
    }

    @Override
    public List<PartnerPost> findPostsByPartnerId(Long partnerId) {
        return partnerPostRepository.findByPartnerId(partnerId);
    }

    @Override
    public List<PartnerPost> findPostsByStatus(PostApprovalStatus status) {
        return partnerPostRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public PartnerPost approvePost(Long postId, Long approvedByEmployeeId, PostApprovalStatus newStatus) {
        PartnerPost post = partnerPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tin đăng."));
        
        // ĐÃ SỬA: So sánh với PostApprovalStatus.PENDING
        if (newStatus == PostApprovalStatus.PENDING_APPROVAL) {
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