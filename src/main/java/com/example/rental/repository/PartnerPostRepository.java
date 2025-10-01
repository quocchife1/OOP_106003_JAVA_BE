package com.example.rental.repository;

import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerPostRepository extends JpaRepository<PartnerPost, Long> {
    // Tìm bài đăng theo ID đối tác
    List<PartnerPost> findByPartnerId(Long partnerId);

    // Tìm bài đăng theo trạng thái duyệt
    List<PartnerPost> findByStatus(PostApprovalStatus status);
}