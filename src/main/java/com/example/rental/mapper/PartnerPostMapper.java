package com.example.rental.mapper;

import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.dto.post.PartnerPostResponse;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;

public class PartnerPostMapper {

    public static PartnerPostResponse toResponse(PartnerPost post) {
        return PartnerPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .area(post.getArea())
                .address(post.getAddress())
                .postType(post.getPostType())
                .status(post.getStatus())
                .partnerId(post.getPartner().getId())
                .createdAt(post.getCreatedAt())
                .approvedAt(post.getApprovedAt())
                .build();
    }
}
