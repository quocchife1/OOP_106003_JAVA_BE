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
    org.springframework.data.domain.Page<Invoice> findByStatus(InvoiceStatus status, org.springframework.data.domain.Pageable pageable);

    org.springframework.data.domain.Page<Invoice> findByDueDateBetween(LocalDate from, LocalDate to, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Invoice> findByDueDateBetweenAndStatus(LocalDate from, LocalDate to, InvoiceStatus status, org.springframework.data.domain.Pageable pageable);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDate date);
    List<Invoice> findByStatusAndDueDateBetween(InvoiceStatus status, LocalDate from, LocalDate to);
    List<Invoice> findByDueDate(LocalDate dueDate);
    // Tìm các hóa đơn theo người thuê (thông qua contract)
    List<Invoice> findByContract_Tenant_Id(Long tenantId);
    org.springframework.data.domain.Page<Invoice> findByContract_Tenant_Id(Long tenantId, org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.Query("""
            select i from Invoice i
              join i.contract c
              join c.room r
             where i.dueDate between :from and :to
               and (:branchId is null or r.branch.id = :branchId)
            """)
        java.util.List<Invoice> findForReport(
            @org.springframework.data.repository.query.Param("from") java.time.LocalDate from,
            @org.springframework.data.repository.query.Param("to") java.time.LocalDate to,
            @org.springframework.data.repository.query.Param("branchId") java.lang.Long branchId);

        @org.springframework.data.jpa.repository.Query("""
            select i from Invoice i
              join i.contract c
              join c.room r
             where i.status = com.example.rental.entity.InvoiceStatus.PAID
               and i.paidDate is not null
               and i.paidDate between :from and :to
               and (:branchId is null or r.branch.id = :branchId)
            """)
        java.util.List<Invoice> findPaidForReport(
            @org.springframework.data.repository.query.Param("from") java.time.LocalDate from,
            @org.springframework.data.repository.query.Param("to") java.time.LocalDate to,
            @org.springframework.data.repository.query.Param("branchId") java.lang.Long branchId);
}
