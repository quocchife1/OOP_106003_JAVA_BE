package com.example.rental.service;

import com.example.rental.entity.User;
import com.example.rental.entity.Role;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> getUserById(Long userId);
    Set<Role> addRoleToUser(Long userId, String roleName);
    Set<Role> removeRoleFromUser(Long userId, String roleName);
}