package com.example.rental.dto.post;

import java.math.BigDecimal;

import com.example.rental.entity.Partners;
import com.example.rental.entity.PostType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PartnerPostRequest {

    @Schema(example = "Phòng trọ giá rẻ gần Đại học Bách Khoa", description = "Tiêu đề của bài đăng")
    private String title;

    @Schema(example = "Phòng trọ rộng 25m², có gác, máy lạnh, wifi miễn phí", description = "Mô tả chi tiết về phòng hoặc căn hộ")
    private String description;

    @Schema(example = "123 Lý Thường Kiệt, Quận 10, TP. Hồ Chí Minh", description = "Địa chỉ nơi cho thuê")
    private String address;

    @Schema(example = "2500000", description = "Giá thuê theo tháng (VNĐ)")
    private BigDecimal price;

    @Schema(example = "25.5", description = "Diện tích (m2)")
    private BigDecimal area;

    @Schema(example = "NORMAL", description = "Loại tin đăng, ví dụ: 'NORMAL' hoặc 'PRIORITY'")
    private PostType postType;

    @Schema(example = "3", description = "ID của đối tác (người đăng bài)")
    private Long partnerId;
}
