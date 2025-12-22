package com.example.rental.service.impl;

import com.example.rental.dto.reservation.ReservationRequest;
import com.example.rental.dto.reservation.ReservationResponse;
import com.example.rental.entity.*;
import com.example.rental.mapper.ReservationMapper;
import com.example.rental.repository.*; // Import wildcard để lấy hết repository
import com.example.rental.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.rental.security.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final ContractRepository contractRepository;
    private final ReservationMapper reservationMapper;
    
    // Đã thêm import repository.* ở trên nên dòng này sẽ hoạt động
    private final GuestRepository guestRepository; 

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }

    // --- Create Methods ---

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

@Override
    @Transactional
    @Audited(action = AuditAction.CREATE_RESERVATION, targetType = "RESERVATION", description = "Tạo yêu cầu giữ phòng")
    public ReservationResponse createReservation(ReservationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Phòng này hiện không có sẵn (Đã thuê hoặc đang bảo trì).");
        }

        String username = getCurrentUsername();
        if (username == null) {
            throw new UsernameNotFoundException("Người dùng chưa xác thực");
        }
        
        // Logic tìm Tenant (đã có hoặc tạo mới từ Guest)
        Tenant tenant = tenantRepository.findByUsername(username)
            .or(() -> tenantRepository.findByUsernameIgnoreCase(username))
            .orElseGet(() -> {
                // Nếu chưa có Tenant -> Tìm Guest và tạo Tenant mới
                Guest guest = guestRepository.findByUsername(username)
                        .or(() -> guestRepository.findByUsernameIgnoreCase(username))
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản người dùng."));
                
                Tenant newTenant = new Tenant();
                newTenant.setUsername(guest.getUsername());
                newTenant.setPassword(guest.getPassword());
                newTenant.setEmail(guest.getEmail());
                newTenant.setFullName(guest.getFullName());
                newTenant.setPhoneNumber(guest.getPhoneNumber());
                newTenant.setStatus(UserStatus.ACTIVE);
                newTenant.setAddress("Cập nhật sau"); 
                
                log.info("Creating new Tenant profile for Guest: {}", username);
                return tenantRepository.save(newTenant);
            });

        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setRoom(room);
        reservation.setTenant(tenant);
        reservation.setReservationCode("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING_CONFIRMATION);
        reservation.setExpirationDate(LocalDateTime.now().plusDays(2)); 

        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Cập nhật trạng thái phòng -> RESERVED
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);
        
        return reservationMapper.toResponse(savedReservation);
    }
    // --- Read Methods ---

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giữ phòng với ID: " + id));
        return reservationMapper.toResponse(reservation);
    }

    @Override
    public List<Reservation> findReservationsByTenantId(Long tenantId) {
        return reservationRepository.findByTenantId(tenantId);
    }

    @Override
    public Page<ReservationResponse> getReservationsByTenant(Long tenantId, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findByTenantId(tenantId, pageable);
        return page.map(reservationMapper::toResponse);
    }

@Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getMyReservations(Pageable pageable) {
        String username = getCurrentUsername();
        if (username == null) throw new UsernameNotFoundException("Unauthenticated");
        
        log.info("Fetching reservations for username: {}", username);

        // Tìm Tenant ID dựa trên username
        Optional<Tenant> tenantOpt = tenantRepository.findByUsername(username)
                .or(() -> tenantRepository.findByUsernameIgnoreCase(username));
        
        if (tenantOpt.isEmpty()) {
            log.warn("User {} has no Tenant profile yet. Returning empty list.", username);
            return Page.empty(pageable);
        }

        Long tenantId = tenantOpt.get().getId();
        log.info("Found Tenant ID: {}", tenantId);

        // Truy vấn DB
        return reservationRepository.findByTenantId(tenantId, pageable)
                .map(reservationMapper::toResponse);
    }    @Override
    public List<Reservation> findReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    @Override
    public Page<ReservationResponse> getReservationsByStatus(String statusStr, Pageable pageable) {
        try {
            ReservationStatus status = ReservationStatus.valueOf(statusStr.toUpperCase());
            Page<Reservation> page = reservationRepository.findByStatus(status, pageable);
            return page.map(reservationMapper::toResponse);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + statusStr);
        }
    }

    @Override
    public List<ReservationResponse> getReservationsByRoom(Long roomId) {
        List<Reservation> list = reservationRepository.findByRoomId(roomId);
        return list.stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // --- Action Methods ---

    @Override
    @Transactional
    @Audited(action = AuditAction.CONFIRM_RESERVATION, targetType = "RESERVATION", description = "Xác nhận giữ phòng")
    public ReservationResponse confirmReservation(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId).orElseThrow();
        r.setStatus(ReservationStatus.RESERVED);
        Room room = r.getRoom();
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);
        return reservationMapper.toResponse(reservationRepository.save(r));
    }

    @Override
    @Transactional
    @Audited(action = AuditAction.CANCEL_RESERVATION, targetType = "RESERVATION", description = "Huỷ giữ phòng")
    public Reservation cancelReservation(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId).orElseThrow();
        if(r.getStatus() == ReservationStatus.RESERVED) {
             Room room = r.getRoom();
             room.setStatus(RoomStatus.AVAILABLE);
             roomRepository.save(room);
        }
        r.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(r);
    }

    @Override
    @Transactional
    @Audited(action = AuditAction.CREATE_CONTRACT, targetType = "CONTRACT", description = "Chuyển giữ phòng thành hợp đồng")
    public Long convertReservationToContract(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu đặt phòng."));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new RuntimeException("Phiếu giữ phòng chưa được xác nhận.");
        }

        Contract contract = new Contract();
        contract.setTenant(reservation.getTenant());
        contract.setRoom(reservation.getRoom());
        
        if (reservation.getStartDate() != null) {
            contract.setStartDate(reservation.getStartDate().toLocalDate());
        } else {
            contract.setStartDate(java.time.LocalDate.now()); 
        }
        
        if (reservation.getEndDate() != null) {
            contract.setEndDate(reservation.getEndDate().toLocalDate());
        }

        Room room = reservation.getRoom();
        if (room != null) {
            if (room.getBranch() != null) {
                contract.setBranchCode(room.getBranch().getBranchCode());
            } else {
                contract.setBranchCode("DEFAULT");
            }
            contract.setRoomNumber(room.getRoomNumber());
            contract.setDeposit(room.getPrice());
        }

        contract.setStatus(ContractStatus.ACTIVE);
        Contract savedContract = contractRepository.save(contract);

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
        
        if (room != null) {
            room.setStatus(RoomStatus.OCCUPIED);
            roomRepository.save(room);
        }

        return savedContract.getId();
    }
}