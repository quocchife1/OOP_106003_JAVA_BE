package com.example.rental.repository;

import com.example.rental.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Long> {
    // Tìm chi tiết hóa đơn theo ID hóa đơn
    List<InvoiceDetail> findByInvoiceId(Long invoiceId);
}