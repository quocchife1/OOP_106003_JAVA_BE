package com.example.rental.mapper;

import com.example.rental.dto.auth.EmployeeRegisterRequest;
import com.example.rental.dto.employee.EmployeeResponse;
import com.example.rental.entity.Employees;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-11T12:17:25+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Autowired
    private BranchMapper branchMapper;

    @Override
    public EmployeeResponse toResponse(Employees employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeResponse.EmployeeResponseBuilder employeeResponse = EmployeeResponse.builder();

        employeeResponse.id( employee.getId() );
        employeeResponse.employeeCode( employee.getEmployeeCode() );
        employeeResponse.username( employee.getUsername() );
        employeeResponse.fullName( employee.getFullName() );
        employeeResponse.email( employee.getEmail() );
        employeeResponse.phoneNumber( employee.getPhoneNumber() );
        employeeResponse.position( employee.getPosition() );
        employeeResponse.hireDate( employee.getHireDate() );
        employeeResponse.status( employee.getStatus() );
        employeeResponse.branch( branchMapper.toResponse( employee.getBranch() ) );

        return employeeResponse.build();
    }

    @Override
    public List<EmployeeResponse> toResponseList(List<Employees> employees) {
        if ( employees == null ) {
            return null;
        }

        List<EmployeeResponse> list = new ArrayList<EmployeeResponse>( employees.size() );
        for ( Employees employees1 : employees ) {
            list.add( toResponse( employees1 ) );
        }

        return list;
    }

    @Override
    public Employees toEntity(EmployeeRegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Employees.EmployeesBuilder employees = Employees.builder();

        employees.phoneNumber( request.getPhone() );
        employees.username( request.getUsername() );
        employees.fullName( request.getFullName() );
        employees.email( request.getEmail() );
        employees.position( request.getPosition() );
        employees.salary( request.getSalary() );
        employees.hireDate( request.getHireDate() );

        return employees.build();
    }
}
