package com.example.rental.repository;

import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Tìm hóa đơn theo ID hợp đồng
    List<Invoice> findByContractId(Long contractId);

    // Tìm hóa đơn theo trạng thái
    List<Invoice> findByStatus(InvoiceStatus status);
}