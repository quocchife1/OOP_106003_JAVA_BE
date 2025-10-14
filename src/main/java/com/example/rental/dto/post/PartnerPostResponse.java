package com.example.rental.dto.post;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.entity.Employees;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.entity.PostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartnerPostResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private BigDecimal area;
    private PostType postType;
    private PostApprovalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private PartnerResponse partnerInfo;
}
