package com.example.rental.dto.contract;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractResponse {
    private Long id;
    private String tenantName;
    private String roomCode;
    private String roomNumber;
    private String branchCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit;
    private String status;
    private LocalDateTime createdAt;
    private String contractFileUrl;
    private String signedContractUrl;
}
