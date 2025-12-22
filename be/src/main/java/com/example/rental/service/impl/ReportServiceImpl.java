package com.example.rental.service.impl;

import com.example.rental.dto.reports.FinancialReportSummaryDTO;
import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceStatus;
import com.example.rental.repository.InvoiceRepository;
import com.example.rental.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public FinancialReportSummaryDTO getSummary(LocalDate from, LocalDate to, Long branchId) {
        List<Invoice> invoiced = invoiceRepository.findForReport(from, to, branchId);
        List<Invoice> paid = invoiceRepository.findPaidForReport(from, to, branchId);

        BigDecimal revenue = invoiced.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidTotal = paid.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = invoiced.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.UNPAID || i.getStatus() == InvoiceStatus.OVERDUE)
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FinancialReportSummaryDTO.builder()
                .revenue(revenue)
                .paid(paidTotal)
                .outstanding(outstanding)
                .invoiceCount(invoiced.size())
                .build();
    }
}
