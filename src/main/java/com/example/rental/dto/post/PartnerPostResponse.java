package com.example.rental.dto.post;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.entity.Employees;
import com.example.rental.entity.PostApprovalStatus;
import com.example.rental.entity.PostType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class PartnerPostResponse {
    private Long postId;
    private String paymentUrl;
}
