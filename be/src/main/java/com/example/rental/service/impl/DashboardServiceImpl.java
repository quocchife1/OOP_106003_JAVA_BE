package com.example.rental.service.impl;

import com.example.rental.dto.dashboard.*;
import com.example.rental.entity.*;
import com.example.rental.repository.*;
import com.example.rental.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final BranchRepository branchRepository;
    
    @Override
    public DirectorDashboardDTO getDirectorDashboard(Long branchId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime yearStart = now.withDayOfYear(1).withHour(0).withMinute(0);
        
        return getDashboardByDateRange(branchId, yearStart, now);
    }
    
    @Override
    public DirectorDashboardDTO getDashboardByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoices = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getCreatedAt().isAfter(startDate) && inv.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        List<Contract> contracts = contractRepository.findAll().stream()
                .filter(c -> c.getCreatedAt().isAfter(startDate) && c.getCreatedAt().isBefore(endDate))
                .collect(Collectors.toList());
        
        List<Room> allRooms = roomRepository.findAll();
        List<Tenant> allTenants = tenantRepository.findAll();
        
        // Revenue calculations
        BigDecimal totalRevenue = invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Debt calculations
        BigDecimal outstandingDebt = invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.UNPAID || inv.getStatus() == InvoiceStatus.OVERDUE)
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long overdueCount = invoices.stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.OVERDUE)
                .count();
        
        // Occupancy rate
        long occupiedCount = allRooms.stream()
                .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                .count();
        Double occupancyRate = allRooms.isEmpty() ? 0.0 : (double) occupiedCount / allRooms.size() * 100;
        
        // Maintenance
        long pendingMaintenance = maintenanceRepository.findAll().stream()
                .filter(m -> m.getStatus() == MaintenanceStatus.PENDING || m.getStatus() == MaintenanceStatus.IN_PROGRESS)
                .count();
        
        return DirectorDashboardDTO.builder()
                .totalRevenueThisMonth(totalRevenue)
                .totalRevenueThisYear(totalRevenue)
                .totalRevenueAllTime(totalRevenue)
                .occupancyRateThisMonth(occupancyRate)
                .totalOutstandingDebt(outstandingDebt)
                .overdueInvoiceCount((int) overdueCount)
                .overdueAmount(invoices.stream()
                        .filter(inv -> inv.getStatus() == InvoiceStatus.OVERDUE)
                        .map(Invoice::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .activeContractCount((int) contracts.stream()
                        .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                        .count())
                .newContractsThisMonth((int) contracts.stream()
                        .filter(c -> c.getStatus() == ContractStatus.PENDING)
                        .count())
                .totalTenantCount((int) allTenants.size())
                .totalRoomCount((int) allRooms.size())
                .availableRoomCount((int) allRooms.stream()
                        .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                        .count())
                .occupiedRoomCount((int) occupiedCount)
                .maintenanceRoomCount((int) allRooms.stream()
                        .filter(r -> r.getStatus() == RoomStatus.MAINTENANCE)
                        .count())
                .pendingMaintenanceCount((int) pendingMaintenance)
                .build();
    }
}
