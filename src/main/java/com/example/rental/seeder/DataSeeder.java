package com.example.rental.seeder;

import com.example.rental.entity.Branch;
import com.example.rental.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    // Nếu sau này có seeder khác, cứ inject thêm Repository vào đây

    @Override
    public void run(String... args) {
        seedBranches();
        // Thêm seed khác sau này:
        // seedUsers();
        // seedVehicles();
    }

    private void seedBranches() {
        if (branchRepository.count() == 0) {
            Branch b1 = Branch.builder()
                    .branchName("Chi nhánh Quận 1")
                    .address("123 Nguyễn Văn Cừ, Quận 5, TP.HCM")
                    .phoneNumber("0281234567")
                    .build();
            branchRepository.save(b1);

            Branch b2 = Branch.builder()
                    .branchName("Chi nhánh Quận 2")
                    .address("456 Lê Lợi, Quận 2, TP.HCM")
                    .phoneNumber("0287654321")
                    .build();
            branchRepository.save(b2);

            // Cập nhật branchCode theo ID cho các bản ghi mới tạo
            branchRepository.findAll().forEach(branch -> {
                if (branch.getBranchCode() == null) {
                    branch.setBranchCode(generateBranchCode(branch.getId()));
                    branchRepository.save(branch);
                }
            });
        }
    }

    private String generateBranchCode(Long id) {
        return String.format("CN%02d", id);
    }
}
