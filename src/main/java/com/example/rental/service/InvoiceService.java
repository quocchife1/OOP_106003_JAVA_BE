package com.example.rental.service;

import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceStatus;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    // Tạo hóa đơn từ một hợp đồng (dựa trên tiền thuê và dịch vụ)
    Invoice generateInvoice(Long contractId);

    // Lấy hóa đơn theo ID
    Optional<Invoice> findById(Long id);

    // Lấy tất cả hóa đơn của một hợp đồng
    List<Invoice> findInvoicesByContractId(Long contractId);
    
    // Lấy hóa đơn theo trạng thái
    List<Invoice> findInvoicesByStatus(InvoiceStatus status);

    // Ghi nhận thanh toán (Chuyển UNPAID sang PAID)
    Invoice markAsPaid(Long invoiceId);
}