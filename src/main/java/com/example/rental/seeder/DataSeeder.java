package com.example.rental.seeder;

import com.example.rental.entity.Branch;
import com.example.rental.entity.Room;
import com.example.rental.entity.RoomStatus;
import com.example.rental.repository.BranchRepository;
import com.example.rental.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        seedBranches();
        seedRooms(); // ✅ Gọi seed room sau khi seed branch
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

            // Gán branchCode sau khi lưu để lấy được ID
            branchRepository.findAll().forEach(branch -> {
                if (branch.getBranchCode() == null) {
                    branch.setBranchCode(generateBranchCode(branch.getId()));
                    branchRepository.save(branch);
                }
            });
        }
    }

    private void seedRooms() {
        if (roomRepository.count() == 0) {
            List<Branch> branches = branchRepository.findAll();
            if (branches.isEmpty()) {
                System.out.println("⚠️ Không có dữ liệu Branch để seed Room!");
                return;
            }

            for (Branch branch : branches) {
                createRoomData(branch, "101", BigDecimal.valueOf(25.5), BigDecimal.valueOf(3500000), "Có máy lạnh, view đẹp");
                createRoomData(branch, "102", BigDecimal.valueOf(20.0), BigDecimal.valueOf(3000000), "Có quạt, gần cửa sổ");
            }
        }
    }

    private void createRoomData(Branch branch, String roomNumber, BigDecimal area, BigDecimal price, String description) {
        String branchCode = branch.getBranchCode();  // vd: CN01
        String roomCode = branchCode + roomNumber;  // vd: CN01101

        Room room = Room.builder()
                .roomCode(roomCode)
                .branchCode(branchCode)
                .roomNumber(roomNumber)
                .area(area)
                .price(price)
                .status(RoomStatus.AVAILABLE)
                .description(description)
                .branch(branch)
                .build();

        roomRepository.save(room);
    }

    private String generateBranchCode(Long id) {
        return String.format("CN%02d", id);
    }
}
