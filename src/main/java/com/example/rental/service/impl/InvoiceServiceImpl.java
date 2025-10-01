package com.example.rental.service.impl;

import com.example.rental.entity.Contract;
import com.example.rental.entity.ContractService;
import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceDetail;
import com.example.rental.entity.InvoiceStatus;
import com.example.rental.repository.ContractRepository;
import com.example.rental.repository.ContractServiceRepository;
import com.example.rental.repository.InvoiceDetailRepository;
import com.example.rental.repository.InvoiceRepository;
import com.example.rental.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;

    @Override
    @Transactional
    public Invoice generateInvoice(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng."));
        
        // 1. Tạo Invoice
        Invoice invoice = Invoice.builder()
                .contract(contract)
                .dueDate(LocalDate.now().plusDays(5)) // Hạn thanh toán 5 ngày
                .status(InvoiceStatus.UNPAID)
                .amount(BigDecimal.ZERO)
                .build();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. Thêm Chi tiết tiền thuê phòng
        InvoiceDetail roomDetail = InvoiceDetail.builder()
                .invoice(savedInvoice)
                .description("Tiền thuê phòng (" + contract.getStartDate().getMonth() + ")")
                .amount(contract.getRoom().getPrice())
                .quantity(1)
                .build();
        invoiceDetailRepository.save(roomDetail);
        totalAmount = totalAmount.add(roomDetail.getAmount());

        // 3. Thêm Chi tiết dịch vụ
        List<ContractService> services = contractServiceRepository.findByContractId(contractId);
        for (ContractService cs : services) {
            BigDecimal servicePrice = cs.getService().getPrice().multiply(BigDecimal.valueOf(cs.getQuantity()));
            InvoiceDetail serviceDetail = InvoiceDetail.builder()
                    .invoice(savedInvoice)
                    .description("Phí dịch vụ: " + cs.getService().getServiceName())
                    .amount(servicePrice)
                    .quantity(cs.getQuantity())
                    .build();
            invoiceDetailRepository.save(serviceDetail);
            totalAmount = totalAmount.add(servicePrice);
        }

        // 4. Cập nhật tổng số tiền vào Invoice
        savedInvoice.setAmount(totalAmount);
        return invoiceRepository.save(savedInvoice);
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> findInvoicesByContractId(Long contractId) {
        return invoiceRepository.findByContractId(contractId);
    }

    @Override
    public List<Invoice> findInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Invoice markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn."));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán rồi.");
        }
        
        // 1. Cập nhật trạng thái
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDate.now());
        
        // 2. Có thể thêm logic ghi nhận vào sổ thu chi ở đây
        
        return invoiceRepository.save(invoice);
    }
}