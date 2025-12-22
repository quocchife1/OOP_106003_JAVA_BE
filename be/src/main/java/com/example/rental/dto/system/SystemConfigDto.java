package com.example.rental.dto.system;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SystemConfigDto {
    private BigDecimal electricPricePerUnit;
    private BigDecimal waterPricePerUnit;
    private BigDecimal lateFeePerDay;
    private LocalDateTime updatedAt;
}
