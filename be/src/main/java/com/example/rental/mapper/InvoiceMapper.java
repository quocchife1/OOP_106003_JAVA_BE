package com.example.rental.mapper;

import com.example.rental.dto.invoice.*;
import com.example.rental.entity.Invoice;
import com.example.rental.entity.InvoiceDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {

    public static InvoiceDetail toDetailEntity(InvoiceDetailRequest req, Invoice invoice) {
        int qty = req.getQuantity() != null ? req.getQuantity() : 1;
        BigDecimal unitPrice = req.getUnitPrice() != null ? req.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(qty));

        return InvoiceDetail.builder()
                .invoice(invoice)
                .description(req.getDescription())
                .unitPrice(unitPrice)
                .quantity(qty)
                .amount(total)
                .build();
    }

    public static InvoiceDetailResponse toDetailResponse(InvoiceDetail d) {
        return InvoiceDetailResponse.builder()
                .id(d.getId())
                .description(d.getDescription())
                .unitPrice(d.getUnitPrice())
                .quantity(d.getQuantity())
                .amount(d.getAmount())
                .build();
    }

    public static InvoiceResponse toResponse(Invoice inv) {
        List<InvoiceDetailResponse> details = inv.getDetails() != null
                ? inv.getDetails().stream().map(InvoiceMapper::toDetailResponse).collect(Collectors.toList())
                : List.of();

        return InvoiceResponse.builder()
                .id(inv.getId())
                .contractId(inv.getContract() != null ? inv.getContract().getId() : null)
                .amount(inv.getAmount())
                .dueDate(inv.getDueDate())
                .paidDate(inv.getPaidDate())
                .status(inv.getStatus() != null ? inv.getStatus().name() : null)
                .createdAt(inv.getCreatedAt())
                .details(details)
                .build();
    }
}
