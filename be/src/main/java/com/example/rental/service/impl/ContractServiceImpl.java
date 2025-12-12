package com.example.rental.service.impl;

import com.example.rental.dto.contract.ContractCreateRequest;
import com.example.rental.entity.*;
import com.example.rental.repository.*;
import com.example.rental.service.ContractService;
import com.example.rental.utils.ContractDocxGenerator;
import com.example.rental.utils.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;
    private final FileStorageService fileStorageService;
    private final ContractDocxGenerator contractDocxGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @com.example.rental.security.Audited(action = com.example.rental.entity.AuditAction.CREATE_CONTRACT, targetType = "CONTRACT", description = "Tạo hợp đồng mới")
    public Contract createContract(ContractCreateRequest request) throws IOException {
        Branch branch = branchRepository.findByBranchCode(request.getBranchCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh: " + request.getBranchCode()));

        Room room = roomRepository.findByBranchCodeAndRoomNumber(request.getBranchCode(), request.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng " + request.getRoomNumber() + " tại chi nhánh " + request.getBranchCode()));

        if (contractRepository.findByRoomIdAndStatus(room.getId(), ContractStatus.ACTIVE) != null)
            throw new RuntimeException("Phòng này đang có hợp đồng hoạt động!");

        Tenant tenant = getOrCreateTenant(request);

        Contract contract = Contract.builder()
                .tenant(tenant)
                .room(room)
                .branchCode(branch.getBranchCode())
                .roomNumber(room.getRoomNumber())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .deposit(request.getDeposit() != null ? request.getDeposit() : BigDecimal.ZERO)
                .status(ContractStatus.PENDING)
                .build();

        Contract saved = contractRepository.save(contract);

        // Mark room as rented immediately when contract is created
        if (room != null) {
            room.setStatus(com.example.rental.entity.RoomStatus.OCCUPIED);
            roomRepository.save(room);
        }

        // ✅ Sinh file Word hợp đồng vào thư mục riêng
        String docxPath = contractDocxGenerator.generateContractFile(saved, request);
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(docxPath).toUriString();

        saved.setContractFileUrl(fileUrl); // link tải xuống trực tiếp
        return contractRepository.save(saved);
    }

    private Tenant getOrCreateTenant(ContractCreateRequest request) {
        if (request.getTenantId() != null)
            return tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người thuê"));

        if (tenantRepository.existsByEmail(request.getTenantEmail()))
            throw new RuntimeException("Email đã tồn tại");
        if (tenantRepository.existsByCccd(request.getTenantCccd()))
            throw new RuntimeException("CCCD đã tồn tại");
        if (tenantRepository.existsByStudentId(request.getStudentId()))
            throw new RuntimeException("Mã sinh viên đã tồn tại");

        String normalizedUsername = request.getTenantEmail() != null ? request.getTenantEmail().trim().toLowerCase() : null;

        Tenant tenant = Tenant.builder()
            .username(normalizedUsername)
            .password(passwordEncoder.encode("123456"))
                .fullName(request.getTenantFullName())
                .email(request.getTenantEmail())
                .phoneNumber(request.getTenantPhoneNumber())
                .address(request.getTenantAddress())
                .cccd(request.getTenantCccd())
                .studentId(request.getStudentId())
                .university(request.getUniversity())
                .status(UserStatus.ACTIVE)
                .build();

        return tenantRepository.save(tenant);
    }

    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    @Override
    public java.util.List<Contract> findByTenantId(Long tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }

    @Override
    public org.springframework.data.domain.Page<Contract> findByTenantId(Long tenantId, org.springframework.data.domain.Pageable pageable) {
        return contractRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    @Transactional
    @com.example.rental.security.Audited(action = com.example.rental.entity.AuditAction.SIGN_CONTRACT, targetType = "CONTRACT", description = "Upload hợp đồng đã ký")
    public Contract uploadSignedContract(Long id, MultipartFile file) throws IOException {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        String filename = fileStorageService.storeFile(file, "contracts");
        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/contracts/")
                .path(filename)
                .toUriString();

        contract.setSignedContractUrl(fileUri);
        contract.setStatus(ContractStatus.ACTIVE);

        // Ensure room is marked as RENTED when contract becomes ACTIVE
        if (contract.getRoom() != null) {
            contract.getRoom().setStatus(com.example.rental.entity.RoomStatus.OCCUPIED);
            roomRepository.save(contract.getRoom());
        }

        return contractRepository.save(contract);
    }

    @Override
    public Resource downloadContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (contract.getContractFileUrl() == null)
            throw new RuntimeException("Hợp đồng chưa có file đính kèm.");

        // ✅ Trỏ đúng file docx
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads/generated_contracts", "contract_" + contract.getId() + ".docx");
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new RuntimeException("File hợp đồng không tồn tại: " + file.getAbsolutePath());
        }

        return new FileSystemResource(file);
    }
}
