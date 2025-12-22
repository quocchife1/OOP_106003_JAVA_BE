package com.example.rental.service.impl;

import com.example.rental.dto.invoice.*;
import com.example.rental.entity.*;
import com.example.rental.mapper.InvoiceMapper;
import com.example.rental.repository.*;
import com.example.rental.security.Audited;
import com.example.rental.service.EmailService;
import com.example.rental.service.InvoiceService;
import com.example.rental.utils.InvoiceEmailTemplateUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ContractRepository contractRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    @Audited(action = AuditAction.CREATE_INVOICE, targetType = "INVOICE", description = "Tạo hóa đơn")
    public InvoiceResponse create(InvoiceRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hợp đồng"));
    
        Invoice inv = Invoice.builder()
                .contract(contract)
                .dueDate(request.getDueDate())
                .status(InvoiceStatus.UNPAID)
                .amount(BigDecimal.ZERO)
                .build();
    
        Invoice savedInvoice = invoiceRepository.save(inv);
    
        List<InvoiceDetail> details = new ArrayList<>();
    
        // 1. Thêm tiền phòng
        if (contract.getRoom() != null) {
            InvoiceDetail roomDetail = InvoiceDetail.builder()
                    .invoice(savedInvoice)
                    .description("Tiền phòng tháng " + request.getDueDate().getMonthValue() + "/" + request.getDueDate().getYear())
                    .unitPrice(contract.getRoom().getPrice())
                    .quantity(1)
                    .amount(contract.getRoom().getPrice())
                    .build();
            details.add(roomDetail);
        }
    
        // 2. Dịch vụ gắn với hợp đồng
        for (ContractService cs : contract.getServices()) {
            RentalServiceItem service = cs.getService();
            BigDecimal unitPrice = service.getPrice();
            Integer quantity = cs.getQuantity();
        
            // Nếu là điện/nước thì tính từ chỉ số công tơ
            if (service.getServiceName().equalsIgnoreCase("Điện") ||
                service.getServiceName().equalsIgnoreCase("Nước")) {
                
                if (cs.getPreviousReading() != null && cs.getCurrentReading() != null) {
                    BigDecimal usage = cs.getCurrentReading().subtract(cs.getPreviousReading());
                    quantity = usage.intValue();
                    // cập nhật previousReading cho tháng sau
                    cs.setPreviousReading(cs.getCurrentReading());
                }
            }
        
            BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
            InvoiceDetail d = InvoiceDetail.builder()
                    .invoice(savedInvoice)
                    .description(service.getServiceName() + " tháng " +
                            request.getDueDate().getMonthValue() + "/" + request.getDueDate().getYear())
                    .unitPrice(unitPrice)
                    .quantity(quantity)
                    .amount(amount)
                    .build();
        
            details.add(d);
        }
    
        BigDecimal total = details.stream()
                .map(InvoiceDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    
        savedInvoice.setAmount(total);
        savedInvoice.setDetails(details);
    
        invoiceDetailRepository.saveAll(details);
        invoiceRepository.save(savedInvoice);
    
        // Gửi mail thông báo hóa đơn mới
        Tenant tenant = contract.getTenant();
        String subject = "[Rental] Hóa đơn mới #" + savedInvoice.getId();
        String html = InvoiceEmailTemplateUtil.buildNewInvoiceEmail(savedInvoice, tenant);
        emailService.sendHtmlMessage(tenant.getEmail(), subject, html);
    
        return InvoiceMapper.toResponse(savedInvoice);
    }


    @Override
    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));
        return InvoiceMapper.toResponse(invoice);
    }

    @Override
    public List<InvoiceResponse> getAll() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<InvoiceResponse> getAll(org.springframework.data.domain.Pageable pageable) {
        return invoiceRepository.findAll(pageable).map(InvoiceMapper::toResponse);
    }

    @Override
    public org.springframework.data.domain.Page<InvoiceResponse> search(
            org.springframework.data.domain.Pageable pageable,
            Integer year,
            Integer month,
            InvoiceStatus status
    ) {
        boolean hasMonth = year != null && month != null;
        if (hasMonth) {
            java.time.LocalDate from = java.time.LocalDate.of(year, month, 1);
            java.time.LocalDate to = from.plusMonths(1).minusDays(1);
            if (status != null) {
                return invoiceRepository.findByDueDateBetweenAndStatus(from, to, status, pageable)
                        .map(InvoiceMapper::toResponse);
            }
            return invoiceRepository.findByDueDateBetween(from, to, pageable)
                    .map(InvoiceMapper::toResponse);
        }

        if (status != null) {
            return invoiceRepository.findByStatus(status, pageable)
                    .map(InvoiceMapper::toResponse);
        }

        return invoiceRepository.findAll(pageable)
                .map(InvoiceMapper::toResponse);
    }

    @Override
    @Transactional
    @Audited(action = AuditAction.CONFIRM_PAYMENT, targetType = "INVOICE", description = "Ghi nhận thanh toán hóa đơn")
    public InvoiceResponse markPaid(Long id, boolean direct) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));
        // If already paid, return existing record (idempotent)
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            return InvoiceMapper.toResponse(invoice);
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDate.now());
        invoiceRepository.save(invoice);

        // ✅ Gửi mail xác nhận thanh toán
        Tenant tenant = invoice.getContract().getTenant();
        String subject = "[Rental] Thanh toán thành công hóa đơn #" + invoice.getId();
        String html = InvoiceEmailTemplateUtil.buildPaymentSuccessEmail(invoice, tenant);
        emailService.sendHtmlMessage(tenant.getEmail(), subject, html);

        return InvoiceMapper.toResponse(invoice);
    }

    @Override
    public void sendReminderForInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));

        Tenant tenant = invoice.getContract().getTenant();
        String subject = "[Rental] Nhắc nhở thanh toán hóa đơn #" + invoice.getId();
        String html = InvoiceEmailTemplateUtil.buildReminderEmail(invoice, tenant);

        emailService.sendHtmlMessage(tenant.getEmail(), subject, html);
    }

    @Override
    @Transactional
    public void markOverdueAndNotify(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn"));

        if (invoice.getStatus() == InvoiceStatus.UNPAID && invoice.getDueDate().isBefore(LocalDate.now())) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);

            Tenant tenant = invoice.getContract().getTenant();
            String subject = "[Rental] Hóa đơn #" + invoice.getId() + " đã quá hạn!";
            String html = InvoiceEmailTemplateUtil.buildOverdueEmail(invoice, tenant);
            emailService.sendHtmlMessage(tenant.getEmail(), subject, html);
        }
    }

    @Override
    @Transactional
    public void checkAndSendDueReminders() {
        LocalDate today = LocalDate.now();
        List<Invoice> invoices = invoiceRepository.findAll();

        for (Invoice inv : invoices) {
            if (inv.getStatus() == InvoiceStatus.UNPAID) {
                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, inv.getDueDate());

                Tenant tenant = inv.getContract().getTenant();
                if (daysLeft == 7 || daysLeft == 3 || daysLeft == 1) {
                    String subject = "[Rental] Nhắc nhở thanh toán hóa đơn #" + inv.getId();
                    String html = InvoiceEmailTemplateUtil.buildReminderEmail(inv, tenant);
                    emailService.sendHtmlMessage(tenant.getEmail(), subject, html);
                }

                // Quá hạn
                if (inv.getDueDate().isBefore(today)) {
                    inv.setStatus(InvoiceStatus.OVERDUE);
                    invoiceRepository.save(inv);

                    String subject = "[Rental] Hóa đơn #" + inv.getId() + " đã quá hạn!";
                    String html = InvoiceEmailTemplateUtil.buildOverdueEmail(inv, tenant);
                    emailService.sendHtmlMessage(tenant.getEmail(), subject, html);
                }
            }
        }
    }

    @Override
    public java.util.List<com.example.rental.dto.invoice.InvoiceResponse> getInvoicesForTenant(Long tenantId) {
        java.util.List<Invoice> invoices = invoiceRepository.findByContract_Tenant_Id(tenantId);
        return invoices.stream().map(InvoiceMapper::toResponse).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<com.example.rental.dto.invoice.InvoiceResponse> getInvoicesForTenant(Long tenantId, org.springframework.data.domain.Pageable pageable) {
        return invoiceRepository.findByContract_Tenant_Id(tenantId, pageable).map(InvoiceMapper::toResponse);
    }
}
