package com.example.rental.repository;

import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
    List<Invoice> findByStatusAndDueDateBetween(InvoiceStatus status, LocalDate from, LocalDate to);
    List<Invoice> findByDueDate(LocalDate dueDate);
    // Tìm các hóa đơn theo người thuê (thông qua contract)
    List<Invoice> findByContract_Tenant_Id(Long tenantId);
    org.springframework.data.domain.Page<Invoice> findByContract_Tenant_Id(Long tenantId, org.springframework.data.domain.Pageable pageable);
}
