package com.example.rental.service;

import com.example.rental.entity.RentalServiceItem;
import java.util.List;
import java.util.Optional;

public interface RentalServiceItemService {
    // Tạo/Cập nhật dịch vụ
    RentalServiceItem save(RentalServiceItem service);

    // Lấy dịch vụ theo ID
    Optional<RentalServiceItem> findById(Long id);

    // Lấy dịch vụ theo mã
    Optional<RentalServiceItem> findByServiceCode(String serviceCode);

    // Lấy tất cả dịch vụ
    List<RentalServiceItem> findAllServices();

    // Xóa dịch vụ
    void deleteById(Long id);
}