package com.example.rental.service;

import com.example.rental.entity.Contract;
import com.example.rental.entity.ContractStatus;
import java.util.List;
import java.util.Optional;

public interface ContractService {
    // Tạo hợp đồng mới
    Contract createContract(Contract contract);

    // Lấy hợp đồng theo ID
    Optional<Contract> findById(Long id);

    // Lấy hợp đồng đang hoạt động theo ID phòng
    Optional<Contract> findActiveContractByRoomId(Long roomId);

    // Lấy tất cả hợp đồng của một người thuê
    List<Contract> findContractsByTenantId(Long tenantId);

    // Chấm dứt hợp đồng (Thay đổi trạng thái)
    Contract terminateContract(Long contractId);
}