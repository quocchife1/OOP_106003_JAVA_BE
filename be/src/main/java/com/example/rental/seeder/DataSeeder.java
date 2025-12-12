package com.example.rental.seeder;

import com.example.rental.entity.*;
import com.example.rental.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * DataSeeder - Khởi tạo dữ liệu mẫu cho hệ thống quản lý cho thuê phòng
 * Dữ liệu là những thông tin thực tế của người Việt
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RentalServiceRepository rentalServiceRepository;
    private final GuestRepository guestRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServicePackageRepository servicePackageRepository;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("========== KHỞI ĐỘNG DATASEEDER ==========");

        try {
            seedBranches();
            seedRooms();
            seedRoomImages(); // Đã cập nhật bộ ảnh mới ổn định hơn
            seedServices();
            seedServicePackages();
            seedEmployees();
            seedGuests();

            System.out.println("========== DATASEEDER HOÀN THÀNH THÀNH CÔNG ==========");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi seed dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== CHI NHÁNH ==========
    private void seedBranches() {
        if (branchRepository.count() > 0) {
            System.out.println("✓ Chi nhánh đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo chi nhánh...");

        // Chi nhánh 1: TP.HCM - Quận 1
        Branch branch1 = Branch.builder()
                .branchName("Ký Túc Xá Quận 1 - TP.HCM")
                .address("123 Nguyễn Huệ, Quận 1, TP.HCM")
                .phoneNumber("0932123456")
                .build();
        branchRepository.save(branch1);

        // Chi nhánh 2: TP.HCM - Quận 3
        Branch branch2 = Branch.builder()
                .branchName("Ký Túc Xá Quận 3 - TP.HCM")
                .address("456 Bà Triệu, Quận 3, TP.HCM")
                .phoneNumber("0934567890")
                .build();
        branchRepository.save(branch2);

        // Chi nhánh 3: TP.HCM - Quận 10
        Branch branch3 = Branch.builder()
                .branchName("Ký Túc Xá Quận 10 - TP.HCM")
                .address("789 Đinh Bộ Lĩnh, Quận 10, TP.HCM")
                .phoneNumber("0936789012")
                .build();
        branchRepository.save(branch3);

        // Tạo mã chi nhánh tự động
        branchRepository.findAll().forEach(branch -> {
            if (branch.getBranchCode() == null) {
                branch.setBranchCode(generateBranchCode(branch.getId()));
                branchRepository.save(branch);
            }
        });

        System.out.println("✓ Tạo thành công " + branchRepository.count() + " chi nhánh");
    }

    // ========== PHÒNG ==========
    private void seedRooms() {
        if (roomRepository.count() > 0) {
            System.out.println("✓ Phòng đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo phòng...");

        List<Branch> branches = branchRepository.findAll();
        if (branches.isEmpty()) {
            System.err.println("❌ Không có chi nhánh để tạo phòng!");
            return;
        }

        int roomCount = 0;

        // Chi nhánh 1: Quận 1
        for (int i = 1; i <= 10; i++) {
            Branch branch = branches.get(0);
            String roomNum = String.format("%02d", i);

            createRoom(branch, roomNum, BigDecimal.valueOf(20 + i),
                    BigDecimal.valueOf(3000000L + (i * 500000)),
                    "Phòng " + i + " - Có máy lạnh, cửa sổ thoáng mát");
            roomCount++;
        }

        // Chi nhánh 2: Quận 3
        for (int i = 1; i <= 8; i++) {
            Branch branch = branches.get(1);
            String roomNum = "2" + String.format("%02d", i);

            createRoom(branch, roomNum, BigDecimal.valueOf(18 + i),
                    BigDecimal.valueOf(2800000L + (i * 400000)),
                    "Phòng " + roomNum + " - Gần trường ĐH, an toàn");
            roomCount++;
        }

        // Chi nhánh 3: Quận 10
        for (int i = 1; i <= 12; i++) {
            Branch branch = branches.get(2);
            String roomNum = "3" + String.format("%02d", i);

            createRoom(branch, roomNum, BigDecimal.valueOf(22 + i),
                    BigDecimal.valueOf(3200000L + (i * 600000)),
                    "Phòng " + roomNum + " - View đẹp, tiện ích đầy đủ");
            roomCount++;
        }

        System.out.println("✓ Tạo thành công " + roomCount + " phòng");
    }

    private void createRoom(Branch branch, String roomNumber, BigDecimal area, BigDecimal price, String description) {
        String branchCode = branch.getBranchCode();
        String roomCode = branchCode + "-" + roomNumber;

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

    // ========== HÌNH ẢNH PHÒNG (FIX LỖI 404) ==========
    private void seedRoomImages() {
        if (roomImageRepository.count() > 0) {
            System.out.println("✓ Hình ảnh phòng đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo hình ảnh phòng (Bộ ảnh ổn định)...");

        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            System.err.println("❌ Không có phòng để thêm hình ảnh!");
            return;
        }

        // --- BỘ ẢNH MỚI (Đã kiểm tra hoạt động) ---

        // Bộ 1: Quận 1 - Hiện đại (Modern / Minimalist)
        List<List<String>> modernImages = Arrays.asList(
            Arrays.asList(
                "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800", // Căn hộ hiện đại
                "https://images.unsplash.com/photo-1522770179533-24471fcdba45?w=800"  // Phòng trống sạch sẽ
            ),
            Arrays.asList(
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800", // Decor tối giản
                "https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800"  // Sofa hiện đại
            )
        );

        // Bộ 2: Quận 3 - Ấm cúng (Cozy / Warm light)
        List<List<String>> cozyImages = Arrays.asList(
            Arrays.asList(
                "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=800", // Giường tầng/KTX
                "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800"  // Góc phòng ấm cúng
            ),
            Arrays.asList(
                "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800", // Phòng ngủ nhỏ
                "https://images.unsplash.com/photo-1540518614846-7eded433c457?w=800"  // Giường ngủ gọn gàng
            )
        );

        // Bộ 3: Quận 10 - Rộng rãi (Spacious / Luxury)
        List<List<String>> spaciousImages = Arrays.asList(
            Arrays.asList(
                "https://images.unsplash.com/photo-1502005229766-3c8ef95562fe?w=800", // Giường đẹp view cửa sổ
                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800"  // Căn hộ rộng
            ),
            Arrays.asList(
                "https://images.unsplash.com/photo-1616594039964-40891a909d99?w=800", // Tông màu kem sáng
                "https://images.unsplash.com/photo-1560185127-6ed189bf02f4?w=800"  // Phòng khách rộng
            )
        );

        int imageCount = 0;

        for (Room room : rooms) {
            List<String> selectedSet;
            String branchName = room.getBranch().getBranchName();
            int randomIndex = new Random().nextInt(2); // Random 0 hoặc 1

            // Chọn bộ ảnh theo chi nhánh
            if (branchName.contains("Quận 1")) {
                selectedSet = modernImages.get(randomIndex);
            } else if (branchName.contains("Quận 3")) {
                selectedSet = cozyImages.get(randomIndex);
            } else {
                selectedSet = spaciousImages.get(randomIndex);
            }

            // --- LƯU 2 HÌNH ẢNH ---
            
            // Hình 1: Thumbnail (Ảnh chính)
            RoomImage img1 = RoomImage.builder()
                    .imageUrl(selectedSet.get(0))
                    .isThumbnail(true)
                    .room(room)
                    .build();
            roomImageRepository.save(img1);

            // Hình 2: Ảnh phụ
            RoomImage img2 = RoomImage.builder()
                    .imageUrl(selectedSet.get(1))
                    .isThumbnail(false)
                    .room(room)
                    .build();
            roomImageRepository.save(img2);

            imageCount += 2;
        }

        System.out.println("✓ Tạo thành công " + imageCount + " hình ảnh thật (Stable Links)");
    }

    // ========== DỊCH VỤ CHO THUÊ ==========
    private void seedServices() {
        if (rentalServiceRepository.count() > 0) {
            System.out.println("✓ Dịch vụ đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo dịch vụ cho thuê...");

        // Điện
        RentalServiceItem electricity = RentalServiceItem.builder()
                .serviceName("Điện")
                .price(BigDecimal.valueOf(3500))
                .unit("kWh")
                .description("Điện công nghiệp, tính theo chỉ số")
                .build();

        // Nước
        RentalServiceItem water = RentalServiceItem.builder()
                .serviceName("Nước")
                .price(BigDecimal.valueOf(20000))
                .unit("m³")
                .description("Nước sạch, tính theo chỉ số")
                .build();

        // Internet
        RentalServiceItem internet = RentalServiceItem.builder()
                .serviceName("Internet")
                .price(BigDecimal.valueOf(150000))
                .unit("phòng/tháng")
                .description("Cáp quang tốc độ 100Mbps, 24/7")
                .build();

        // Giữ xe
        RentalServiceItem parking = RentalServiceItem.builder()
                .serviceName("Giữ xe máy")
                .price(BigDecimal.valueOf(50000))
                .unit("xe/tháng")
                .description("Giữ xe máy an toàn trong hầm xe")
                .build();

        // Vệ sinh
        RentalServiceItem cleaning = RentalServiceItem.builder()
                .serviceName("Vệ sinh chung cư")
                .price(BigDecimal.valueOf(60000))
                .unit("phòng/tháng")
                .description("Vệ sinh hành lang, cầu thang hàng tuần")
                .build();

        // Bảo vệ
        RentalServiceItem security = RentalServiceItem.builder()
                .serviceName("Bảo vệ 24/7")
                .price(BigDecimal.valueOf(80000))
                .unit("phòng/tháng")
                .description("Nhân viên bảo vệ 24 giờ")
                .build();

        // Nước nóng
        RentalServiceItem hotWater = RentalServiceItem.builder()
                .serviceName("Nước nóng")
                .price(BigDecimal.valueOf(100000))
                .unit("phòng/tháng")
                .description("Bình nước nóng sử dụng năng lượng mặt trời")
                .build();

        rentalServiceRepository.saveAll(List.of(electricity, water, internet, parking, cleaning, security, hotWater));

        System.out.println("✓ Tạo thành công " + rentalServiceRepository.count() + " dịch vụ");
    }

    // ========== GÓI DỊCH VỤ TIN ĐĂNG (PARTNER) ==========
    private void seedServicePackages() {
        if (servicePackageRepository.count() > 0) {
            System.out.println("✓ Gói dịch vụ đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo gói dịch vụ tin đăng cho đối tác...");

        ServicePackage basic = ServicePackage.builder()
                .name("Gói thường")
                .price(new java.math.BigDecimal("50000"))
                .durationDays(7)
                .description("Hiển thị cơ bản trong 7 ngày")
                .isActive(true)
                .build();

        ServicePackage priority = ServicePackage.builder()
                .name("Gói ưu tiên")
                .price(new java.math.BigDecimal("150000"))
                .durationDays(7)
                .description("Ưu tiên hiển thị, đẩy tin trong 7 ngày")
                .isActive(true)
                .build();

        servicePackageRepository.saveAll(java.util.List.of(basic, priority));
        System.out.println("✓ Tạo thành công " + servicePackageRepository.count() + " gói dịch vụ");
    }

    // ========== KHÁCH ==========
    private void seedGuests() {
        if (guestRepository.count() > 0) {
            System.out.println("✓ Khách đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo khách...");

        // Khách 1
        Guest guest1 = Guest.builder()
                .username("guest_john")
                .password("$2a$10$9dXeK5.KqNK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K")
                .fullName("Trần Minh Đức")
                .email("minh.duc@gmail.com")
                .phoneNumber("0971234567")
                .status(UserStatus.ACTIVE)
                .build();
        guestRepository.save(guest1);

        // Khách 2
        Guest guest2 = Guest.builder()
                .username("guest_jane")
                .password("$2a$10$9dXeK5.KqNK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K")
                .fullName("Lê Thị Hương")
                .email("huong.le@gmail.com")
                .phoneNumber("0972345678")
                .status(UserStatus.ACTIVE)
                .build();
        guestRepository.save(guest2);

        // Khách 3
        Guest guest3 = Guest.builder()
                .username("guest_peter")
                .password("$2a$10$9dXeK5.KqNK5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K5K")
                .fullName("Phạm Quốc Bảo")
                .email("quoc.bao@gmail.com")
                .phoneNumber("0973456789")
                .status(UserStatus.ACTIVE)
                .build();
        guestRepository.save(guest3);

        System.out.println("✓ Tạo thành công " + guestRepository.count() + " khách");
    }

    // ========== NHÂN VIÊN ==========
    private void seedEmployees() {
        if (employeeRepository.count() > 0) {
            System.out.println("✓ Nhân viên đã tồn tại, bỏ qua...");
            return;
        }

        System.out.println("⚙ Đang tạo nhân viên...");

        List<Branch> branches = branchRepository.findAll();

        // Encode password 123456 at runtime to ensure matches application's PasswordEncoder
        String hashedPassword = passwordEncoder.encode("123456");

        // Admin: admin / 123456
        Employees admin = Employees.builder()
                .username("admin")
                .employeeCode("EMP001")
                .password(hashedPassword)
                .fullName("Quản Trị Viên Hệ Thống")
                .email("admin@rentalsystem.com")
                .phoneNumber("0900000000")
                .position(EmployeePosition.ADMIN)
                .status(UserStatus.ACTIVE)
                .branch(branches.isEmpty() ? null : branches.get(0))
                .build();
        employeeRepository.save(admin);
        System.out.println("✓ Seeded admin credentials -> username: admin , password: 123456");

        // Manager
        Employees manager = Employees.builder()
                .username("manager")
                .employeeCode("EMP002")
                .password(hashedPassword)
                .fullName("Nguyễn Văn Quản Lý")
                .email("manager@rentalsystem.com")
                .phoneNumber("0901111111")
                .position(EmployeePosition.MANAGER)
                .status(UserStatus.ACTIVE)
                .branch(branches.size() > 0 ? branches.get(0) : null)
                .build();
        employeeRepository.save(manager);

        // Accountant
        Employees accountant = Employees.builder()
                .username("accountant")
                .employeeCode("EMP003")
                .password(hashedPassword)
                .fullName("Trần Thị Kế Toán")
                .email("accountant@rentalsystem.com")
                .phoneNumber("0902222222")
                .position(EmployeePosition.ACCOUNTANT)
                .status(UserStatus.ACTIVE)
                .branch(branches.size() > 0 ? branches.get(0) : null)
                .build();
        employeeRepository.save(accountant);

        // Maintenance Staff
        Employees maintenance = Employees.builder()
                .username("maintenance")
                .employeeCode("EMP004")
                .password(hashedPassword)
                .fullName("Lê Văn Bảo Trì")
                .email("maintenance@rentalsystem.com")
                .phoneNumber("0903333333")
                .position(EmployeePosition.MAINTENANCE)
                .status(UserStatus.ACTIVE)
                .branch(branches.size() > 1 ? branches.get(1) : null)
                .build();
        employeeRepository.save(maintenance);

        // Receptionist
        Employees receptionist = Employees.builder()
                .username("receptionist")
                .employeeCode("EMP005")
                .password(hashedPassword)
                .fullName("Phạm Thị Lễ Tân")
                .email("receptionist@rentalsystem.com")
                .phoneNumber("0904444444")
                .position(EmployeePosition.RECEPTIONIST)
                .status(UserStatus.ACTIVE)
                .branch(branches.size() > 2 ? branches.get(2) : null)
                .build();
        employeeRepository.save(receptionist);

        System.out.println("✓ Tạo thành công " + employeeRepository.count() + " nhân viên (bao gồm admin)");
    }

    // ========== HELPER ==========
    private String generateBranchCode(Long id) {
        return String.format("CN%02d", id);
    }
}