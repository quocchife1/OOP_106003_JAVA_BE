package com.example.rental.service;

import com.example.rental.dto.auth.AuthLoginRequest;
import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.auth.AuthResponse;
import com.example.rental.entity.Role;
import com.example.rental.entity.User;
import com.example.rental.repository.RoleRepository;
import com.example.rental.repository.UserRepository;
import com.example.rental.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public void register(AuthRegisterRequest request) {
        // Kiểm tra username, email, phone đã tồn tại chưa
        if (userRepository.findByUsername(request.getUsername()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent()
                || userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Người dùng đã tồn tại");
        }

        // Tìm role mặc định
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role 'USER' not found"));
        
        LocalDate dob = null;
        if (request.getDob() != null) {
            dob = LocalDate.parse(request.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
            
        // Tạo người dùng mới
        User newUser = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .dob(dob)
                .status("ACTIVE")
                .role(userRole)
                .build();

        userRepository.save(newUser);
    }

    public AuthResponse login(AuthLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateAccessToken(authentication);
        return AuthResponse.builder().accessToken(token).build();
    }
}