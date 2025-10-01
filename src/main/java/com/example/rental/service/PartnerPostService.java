package com.example.rental.service;

import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostApprovalStatus;
import java.util.List;
import java.util.Optional;

public interface PartnerPostService {
    // Đối tác tạo tin đăng mới (Trạng thái ban đầu là PENDING)
    PartnerPost createPost(PartnerPost post);

    // Lấy tin đăng theo ID
    Optional<PartnerPost> findById(Long id);

    // Lấy tin đăng theo ID đối tác
    List<PartnerPost> findPostsByPartnerId(Long partnerId);

    // Lấy tin đăng theo trạng thái (Dành cho Admin duyệt)
    List<PartnerPost> findPostsByStatus(PostApprovalStatus status);

    // Nhân viên duyệt tin (APPROVED/REJECTED)
    PartnerPost approvePost(Long postId, Long approvedByEmployeeId, PostApprovalStatus newStatus);
}