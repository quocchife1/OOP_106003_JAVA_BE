package com.example.rental.repository;

import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface PartnerPostRepository extends JpaRepository<PartnerPost, Long> {
    // Lấy danh sách bài đăng theo đối tác, ưu tiên PRIORITY lên trên
    @Query("""
            SELECT p FROM PartnerPost p
            WHERE p.partner.id = :partnerId
            ORDER BY
                CASE WHEN p.postType = 'PRIORITY' THEN 0 ELSE 1 END,
                p.createdAt DESC
            """)
    List<PartnerPost> findByPartnerId(Long partnerId);

    // Lấy danh sách bài đăng theo trạng thái duyệt, ưu tiên PRIORITY lên trên
    @Query("""
            SELECT p FROM PartnerPost p
            WHERE p.status = :status
            ORDER BY
                CASE WHEN p.postType = 'PRIORITY' THEN 0 ELSE 1 END,
                p.createdAt DESC
            """)
    List<PartnerPost> findByStatus(PostApprovalStatus status);

    Optional<PartnerPost> findByOrderId(String orderId);
}