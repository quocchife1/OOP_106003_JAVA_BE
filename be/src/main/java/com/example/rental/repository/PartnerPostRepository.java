package com.example.rental.repository;

import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.PostApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerPostRepository extends JpaRepository<PartnerPost, Long> {
    // Tìm bài đăng theo ID đối tác (loại bỏ bản ghi đã xóa)
    List<PartnerPost> findByPartnerIdAndIsDeletedFalse(Long partnerId);

    // Phân trang bài đăng theo ID đối tác (loại bỏ bản ghi đã xóa)
    Page<PartnerPost> findByPartnerIdAndIsDeletedFalse(Long partnerId, Pageable pageable);

    // Tìm bài đăng theo trạng thái duyệt (loại bỏ bản ghi đã xóa)
    List<PartnerPost> findByStatusAndIsDeletedFalse(PostApprovalStatus status);

    // Phân trang theo tập trạng thái (ví dụ APPROVED/ACTIVE), loại bỏ bản ghi đã xóa
    Page<PartnerPost> findByStatusInAndIsDeletedFalse(List<PostApprovalStatus> statuses, Pageable pageable);

    // Phân trang theo tập trạng thái + tìm theo tiêu đề (contains, ignore case), loại bỏ bản ghi đã xóa
    Page<PartnerPost> findByStatusInAndTitleContainingIgnoreCaseAndIsDeletedFalse(List<PostApprovalStatus> statuses, String title, Pageable pageable);

    // Counters for stats
    long countByStatusAndIsDeletedFalse(PostApprovalStatus status);
    long countByStatusInAndIsDeletedFalse(List<PostApprovalStatus> statuses);
    long countByApprovedAtBetweenAndIsDeletedFalse(java.time.LocalDateTime start, java.time.LocalDateTime end);
}