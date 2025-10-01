package com.example.rental.repository;

import com.example.rental.entity.ContractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractServiceRepository extends JpaRepository<ContractService, Long> {
    // Tìm danh sách dịch vụ đã đăng ký theo ID hợp đồng
    List<ContractService> findByContractId(Long contractId);
}