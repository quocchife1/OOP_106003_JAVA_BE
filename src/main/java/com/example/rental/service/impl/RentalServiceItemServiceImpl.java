package com.example.rental.service.impl;

import com.example.rental.repository.RentalServiceItemRepository; // Đã đổi tên Repository
import com.example.rental.service.RentalServiceItemService; // Đã đổi tên Service Interface
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Import Entity đã được đổi tên
import com.example.rental.entity.RentalServiceItem; 

@Service
@RequiredArgsConstructor
// Cập nhật tên lớp và triển khai interface mới
public class RentalServiceItemServiceImpl implements RentalServiceItemService {

    private final RentalServiceItemRepository serviceRepository; // Cập nhật tên biến nếu cần thiết

    @Override
    @Transactional
    public RentalServiceItem save(RentalServiceItem service) {
        // Logic nghiệp vụ: Kiểm tra trùng serviceCode
        return serviceRepository.save(service);
    }

    @Override
    public Optional<RentalServiceItem> findById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public Optional<RentalServiceItem> findByServiceCode(String serviceCode) {
        // Nếu Repository đã được sửa thành Optional<RentalServiceItem>, bạn dùng .orElse(null)
        // Nếu Repository vẫn trả về RentalServiceItem (null nếu không tìm thấy):
        RentalServiceItem service = serviceRepository.findByServiceCode(serviceCode);
        return Optional.ofNullable(service); 
    }

    @Override
    public List<RentalServiceItem> findAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        serviceRepository.deleteById(id);
    }
}