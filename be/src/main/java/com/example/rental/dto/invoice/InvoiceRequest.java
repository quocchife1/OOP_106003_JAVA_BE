package com.example.rental.dto.invoice;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequest {
    private Long contractId;
    private LocalDate dueDate;
    private List<InvoiceDetailRequest> details;
}
