package com.example.rental.repository;

import com.example.rental.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, Long> {
    
    // Đã có sẵn: Trả về Optional
    Optional<Employees> findByUsername(String username);

    // Đã có sẵn: Trả về Optional
    Optional<Employees> findByEmployeeCode(String employeeCode);

    // NEW: Bổ sung cho hàm checkUserExistence trong AuthServiceImpl
    boolean existsByUsername(String username);

    // NEW: Bổ sung cho hàm checkUserExistence trong AuthServiceImpl
    boolean existsByEmail(String email);
}