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
                .address(post.getAddress())
                .price(post.getPrice())
                .area(post.getArea())
                .postType(post.getPostType())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .partnerInfo(mapPartnerToResponse(post.getPartner()))
                .build();
    }

    private static PartnerResponse mapPartnerToResponse(Partners partner) {
        if (partner == null)
            return null;
        return PartnerResponse.builder()
                .id(partner.getId())
                .username(partner.getUsername())
                .email(partner.getEmail())
                .phoneNumber(partner.getPhoneNumber())
                .build();
    }
}
