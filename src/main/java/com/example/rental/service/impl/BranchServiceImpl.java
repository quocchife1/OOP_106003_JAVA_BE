package com.example.rental.service.impl;

import com.example.rental.dto.branch.BranchRequest;
import com.example.rental.dto.branch.BranchResponse;
import com.example.rental.entity.Branch;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.mapper.BranchMapper;
import com.example.rental.repository.BranchRepository;
import com.example.rental.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    // ===== ENTITY LAYER =====
    @Override
    public Optional<Branch> findById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    public Optional<Branch> findByBranchCode(String branchCode) {
        return branchRepository.findByBranchCode(branchCode);
    }

    // ===== DTO LAYER =====
    @Override
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(branchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));
        return branchMapper.toResponse(branch);
    }

    @Override
    public BranchResponse getBranchByCode(String branchCode) {
        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "branchCode", branchCode));
        return branchMapper.toResponse(branch);
    }

    @Override
    public BranchResponse createBranch(BranchRequest request) {
        // B1: Chuyển từ DTO sang entity
        Branch branch = branchMapper.toEntity(request);

        // B2: Lưu trước để có ID
        Branch saved = branchRepository.save(branch);

        // B3: Sinh mã branchCode theo ID
        String code = String.format("CN%02d", saved.getId());
        saved.setBranchCode(code);

        // B4: Lưu lại
        saved = branchRepository.save(saved);

        // B5: Trả về DTO
        return branchMapper.toResponse(saved);
    }

    @Override
    public BranchResponse updateBranch(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", id));

        // Cập nhật các field khác
        branchMapper.updateEntityFromRequest(request, branch);

        // Không cho chỉnh lại branchCode từ request (vì tự động sinh)
        // Nếu bạn muốn cho người dùng sửa, bỏ đoạn này

        Branch updated = branchRepository.save(branch);
        return branchMapper.toResponse(updated);
    }

    @Override
    public void deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch", "id", id);
        }
        branchRepository.deleteById(id);
    }
}
