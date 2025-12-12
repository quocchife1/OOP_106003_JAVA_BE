package com.example.rental.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyRevenueDTO {
    private Integer month;
    private Integer year;
    private BigDecimal revenue;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class RoomOccupancyDTO {
    private Long branchId;
    private String branchName;
    private Double occupancyRate;
    private Integer totalRooms;
    private Integer occupiedRooms;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class OverdueInvoiceDTO {
    private Long invoiceId;
    private Long contractId;
    private String tenantName;
    private BigDecimal amount;
    private Long daysOverdue;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ContractSummaryDTO {
    private Long contractId;
    private String tenantName;
    private String roomInfo;
    private String endDate;
    private Integer daysRemaining;
}
