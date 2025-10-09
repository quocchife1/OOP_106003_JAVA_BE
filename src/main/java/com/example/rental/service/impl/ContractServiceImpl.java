package com.example.rental.service.impl;

import com.example.rental.dto.contract.ContractCreateRequest;
import com.example.rental.entity.*;
import com.example.rental.repository.*;
import com.example.rental.service.ContractService;
import com.example.rental.utils.ContractPdfGenerator;
import com.example.rental.utils.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final ContractPdfGenerator contractPdfGenerator;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
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

        // ✅ Sinh file PDF hợp đồng thật vào thư mục riêng
        String pdfPath = contractPdfGenerator.generateContractFile(saved);
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(pdfPath).toUriString();

        saved.setContractFileUrl(fileUrl);
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

        Tenant tenant = Tenant.builder()
                .username(request.getTenantEmail())
                .password(passwordEncoder.encode("123"))
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
    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    @Transactional
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

        return contractRepository.save(contract);
    }

    @Override
    public Resource downloadContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));

        if (contract.getContractFileUrl() == null)
            throw new RuntimeException("Hợp đồng chưa có file đính kèm.");

        // ✅ Trỏ đúng tới thư mục mới
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads/generated_contracts", "contract_" + contract.getId() + ".pdf");
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new RuntimeException("File hợp đồng không tồn tại: " + file.getAbsolutePath());
        }

        return new FileSystemResource(file);
    }
}
