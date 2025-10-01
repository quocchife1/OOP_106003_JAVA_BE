package com.example.rental.service.impl;

import com.example.rental.entity.Contract;
import com.example.rental.entity.ContractStatus;
import com.example.rental.entity.RoomStatus;
import com.example.rental.repository.ContractRepository;
import com.example.rental.repository.RoomRepository; // Cần RoomRepository để cập nhật trạng thái phòng
import com.example.rental.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final RoomRepository roomRepository; 

    @Override
    @Transactional
    public Contract createContract(Contract contract) {
        // Logic nghiệp vụ: Đảm bảo phòng chưa có hợp đồng ACTIVE nào khác
        if (findActiveContractByRoomId(contract.getRoom().getId()).isPresent()) {
            throw new RuntimeException("Phòng này hiện đang có hợp đồng hoạt động.");
        }

        // 1. Lưu hợp đồng
        contract.setStatus(ContractStatus.ACTIVE);
        Contract savedContract = contractRepository.save(contract);
        
        // 2. Cập nhật trạng thái phòng thành Đang thuê (OCCUPIED)
        savedContract.getRoom().setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(savedContract.getRoom());
        
        return savedContract;
    }

    @Override
    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    @Override
    public Optional<Contract> findActiveContractByRoomId(Long roomId) {
        Contract contract = contractRepository.findByRoomIdAndStatus(roomId, ContractStatus.ACTIVE);
        // Chuyển kết quả sang Optional để nhất quán
        return Optional.ofNullable(contract); 
    }

    @Override
    public List<Contract> findContractsByTenantId(Long tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional
    public Contract terminateContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng để chấm dứt."));
        
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new RuntimeException("Hợp đồng không ở trạng thái ACTIVE để chấm dứt.");
        }

        // 1. Chấm dứt hợp đồng
        contract.setStatus(ContractStatus.ENDED);
        contract.setEndDate(LocalDate.now());
        Contract endedContract = contractRepository.save(contract);
        
        // 2. Cập nhật trạng thái phòng thành Trống (AVAILABLE)
        endedContract.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(endedContract.getRoom());
        
        return endedContract;
    }
}