package com.example.rental.mapper;

import com.example.rental.dto.contract.ContractResponse;
import com.example.rental.entity.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {
    public ContractResponse toResponse(Contract contract) {
        ContractResponse dto = new ContractResponse();
        dto.setId(contract.getId());
        dto.setTenantName(contract.getTenant() != null ? contract.getTenant().getFullName() : null);

        // roomCode (nếu cần)
        dto.setRoomCode(contract.getRoom() != null ? contract.getRoom().getRoomCode() : null);

        // branchCode & roomNumber: ưu tiên giá trị lưu trong contract (snapshot)
        if (contract.getBranchCode() != null) {
            dto.setBranchCode(contract.getBranchCode());
        } else if (contract.getRoom() != null) {
            dto.setBranchCode(contract.getRoom().getBranchCode());
        }

        if (contract.getRoomNumber() != null) {
            dto.setRoomNumber(contract.getRoomNumber());
        } else if (contract.getRoom() != null) {
            dto.setRoomNumber(contract.getRoom().getRoomNumber());
        }

        dto.setStartDate(contract.getStartDate());
        dto.setEndDate(contract.getEndDate());
        dto.setDeposit(contract.getDeposit());
        dto.setStatus(contract.getStatus() != null ? contract.getStatus().name() : null);
        dto.setCreatedAt(contract.getCreatedAt());
        dto.setContractFileUrl(contract.getContractFileUrl());
        dto.setSignedContractUrl(contract.getSignedContractUrl());
        return dto;
    }
}
