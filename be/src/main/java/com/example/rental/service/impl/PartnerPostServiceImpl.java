package com.example.rental.service.impl;

import com.example.rental.entity.Employees;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.repository.PartnerPostRepository;
import com.example.rental.service.PartnerPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    @Transactional
    public PartnerPost createPost(PartnerPost post) {
        // ĐÃ SỬA: Sử dụng PostApprovalStatus.PENDING
        post.setStatus(PostApprovalStatus.PENDING_APPROVAL);
        return partnerPostRepository.save(post);
    }

    @Override
    public Optional<PartnerPost> findById(Long id) {
        return partnerPostRepository.findById(id);
    }

    @Override
    public List<PartnerPost> findPostsByPartnerId(Long partnerId) {
        return partnerPostRepository.findByPartnerIdAndIsDeletedFalse(partnerId);
    }

    @Override
    public Page<PartnerPost> findPostsByPartnerId(Long partnerId, Pageable pageable) {
        return partnerPostRepository.findByPartnerIdAndIsDeletedFalse(partnerId, pageable);
    }

    @Override
    public List<PartnerPost> findPostsByStatus(PostApprovalStatus status) {
        return partnerPostRepository.findByStatusAndIsDeletedFalse(status);
    }

    @Override
    public Page<PartnerPost> findPublicVisiblePosts(Pageable pageable) {
        return partnerPostRepository.findByStatusInAndIsDeletedFalse(
                java.util.List.of(PostApprovalStatus.APPROVED), pageable);
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

    @Override
    @Transactional
    public PartnerPost updatePost(PartnerPost post) {
        // Reset trạng thái về PENDING_APPROVAL khi chỉnh sửa
        post.setStatus(PostApprovalStatus.PENDING_APPROVAL);
        post.setApprovedBy(null);
        post.setApprovedAt(null);
        return partnerPostRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        partnerPostRepository.findById(id).ifPresent(post -> {
            post.setDeleted(true);
            partnerPostRepository.save(post);
        });
    }
}