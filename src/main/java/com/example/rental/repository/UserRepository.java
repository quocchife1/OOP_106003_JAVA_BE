package com.example.rental.repository;

import com.example.rental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
}