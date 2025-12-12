package com.example.rental.service;

import com.example.rental.dto.invoice.InvoiceRequest;
import com.example.rental.dto.invoice.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse create(InvoiceRequest request);
    InvoiceResponse getById(Long id);
    List<InvoiceResponse> getAll();
    Page<InvoiceResponse> getAll(Pageable pageable);
    InvoiceResponse markPaid(Long id, boolean direct);
    void sendReminderForInvoice(Long id);
    void markOverdueAndNotify(Long id);

    // Thêm mới để scheduler gọi
    void checkAndSendDueReminders();

    // Lấy hóa đơn dành cho 1 người thuê (tenant)
    java.util.List<com.example.rental.dto.invoice.InvoiceResponse> getInvoicesForTenant(Long tenantId);
    org.springframework.data.domain.Page<com.example.rental.dto.invoice.InvoiceResponse> getInvoicesForTenant(Long tenantId, org.springframework.data.domain.Pageable pageable);
}
