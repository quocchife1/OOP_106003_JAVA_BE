package com.example.rental.repository;

import com.example.rental.entity.Contract;
import com.example.rental.entity.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Tìm kiếm hợp đồng theo ID người thuê
    List<Contract> findByTenantId(Long tenantId);

    // Tìm kiếm hợp đồng theo ID phòng
    Contract findByRoomIdAndStatus(Long roomId, ContractStatus status);
}