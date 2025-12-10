package com.example.rental.mapper;

import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.dto.post.PartnerPostResponse;
import com.example.rental.entity.PartnerPost;
import com.example.rental.entity.Partners;

public class PartnerPostMapper {

    public static PartnerPostResponse toResponse(Long id, String paymentUrl) {
        return PartnerPostResponse.builder()
                .postId(id)
                .paymentUrl(paymentUrl)
                .build();
    }
}
