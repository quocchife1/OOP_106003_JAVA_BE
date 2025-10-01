package com.example.rental.service.impl;

import com.example.rental.dto.auth.EmployeeRegisterRequest;
import com.example.rental.dto.employee.EmployeeResponse;
import com.example.rental.entity.Branch;
import com.example.rental.entity.Employees;
import com.example.rental.entity.UserStatus;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.mapper.EmployeeMapper;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.service.BranchService;
import com.example.rental.service.EmployeeService;
import com.example.rental.service.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchService branchService; 
    private final CodeGenerator codeGenerator;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper; 

    @Override
    @Transactional
    public EmployeeResponse registerNewEmployee(EmployeeRegisterRequest request) {
        // 1. Tìm Entity Branch từ BranchService
        Branch branch = branchService.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch", "id", request.getBranchId())); 

        // 2. Chuyển đổi DTO sang Entity (chưa có password, branch, code)
        Employees newEmployee = employeeMapper.toEntity(request);
        
        // 3. Thiết lập các trường nghiệp vụ và bảo mật
        newEmployee.setPassword(passwordEncoder.encode(request.getPassword()));
        newEmployee.setBranch(branch); 
        newEmployee.setStatus(UserStatus.ACTIVE);

        // 4. Lưu lần 1 để có ID
        Employees savedEmployee = employeeRepository.save(newEmployee);
        
        // 5. Sinh mã và cập nhật (NVxxxx)
        String employeeCode = codeGenerator.generateCode("NV", savedEmployee.getId());
        savedEmployee.setEmployeeCode(employeeCode);
        
        // 6. Lưu lần 2 và trả về DTO Response
        Employees finalEmployee = employeeRepository.save(savedEmployee);
        return employeeMapper.toResponse(finalEmployee);
    }
    

    @Override
    public Optional<Employees> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employees> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public List<Employees> findAllEmployees() {
        return employeeRepository.findAll();
    }

    // ĐÃ XÓA: Phương thức updateEmployeeProfile(Employees employee) cũ, nên dùng DTO mới

    @Override
    @Transactional
    public Employees updateStatus(Long employeeId, UserStatus status) {
        Employees employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId)); 
        employee.setStatus(status);
        return employeeRepository.save(employee);
    }
}