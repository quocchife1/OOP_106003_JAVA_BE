package com.example.rental.service;

import com.example.rental.entity.Employees;
import com.example.rental.entity.Guest;
import com.example.rental.entity.Partners;
import com.example.rental.entity.Tenant;
import com.example.rental.repository.EmployeeRepository;
import com.example.rental.repository.GuestRepository;
import com.example.rental.repository.PartnerRepository;
import com.example.rental.repository.TenantRepository;
import com.example.rental.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final GuestRepository guestRepository;
    private final TenantRepository tenantRepository;
    private final PartnerRepository partnerRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Tìm trong Guest (Sử dụng Optional<Guest> rõ ràng)
        Optional<Guest> guestOptional = guestRepository.findByUsername(username);
        if (guestOptional.isPresent()) {
            Guest user = guestOptional.get();
            return new CustomUserDetails(user.getUsername(), user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST")), "GUEST");
        }

        // 2. Tìm trong Tenant (Sử dụng Optional<Tenant> rõ ràng)
        Optional<Tenant> tenantOptional = tenantRepository.findByUsername(username);
        if (tenantOptional.isPresent()) {
            Tenant user = tenantOptional.get();
            return new CustomUserDetails(user.getUsername(), user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_TENANT")), "TENANT");
        }
        
        // 3. Tìm trong Partners (Sử dụng Optional<Partners> rõ ràng)
        Optional<Partners> partnerOptional = partnerRepository.findByUsername(username);
        if (partnerOptional.isPresent()) {
            Partners user = partnerOptional.get();
            return new CustomUserDetails(user.getUsername(), user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTNER")), "PARTNER");
        }
        
        // 4. Tìm trong Employees (Sử dụng Optional<Employees> rõ ràng)
        Optional<Employees> employeeOptional = employeeRepository.findByUsername(username);
        if (employeeOptional.isPresent()) {
            Employees user = employeeOptional.get();
            // Lấy vai trò chi tiết hơn
            String roleName = "ROLE_" + user.getPosition().name(); 
            return new CustomUserDetails(user.getUsername(), user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(roleName)), "EMPLOYEE");
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}