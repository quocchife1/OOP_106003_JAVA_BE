package com.example.rental.repository;

import com.example.rental.entity.RentalServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalServiceItemRepository extends JpaRepository<RentalServiceItem, Long> {
    // Tìm dịch vụ theo mã dịch vụ
    RentalServiceItem findByServiceCode(String serviceCode);
}