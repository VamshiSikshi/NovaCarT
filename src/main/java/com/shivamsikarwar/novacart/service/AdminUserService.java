package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.UserResponseDto;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserService userService;

    // =========================
    // GET ALL USERS
    // =========================

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(
            Pageable pageable) {

        return userRepository
                .findAllByOrderByIdDesc(pageable)
                .map(userService::toResponseDto);
    }

    // =========================
    // GET USERS BY ROLE
    // =========================

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsersByRole(
            Role role,
            Pageable pageable) {

        return userRepository
                .findByRoleOrderByIdDesc(
                        role,
                        pageable
                )
                .map(userService::toResponseDto);
    }

    // =========================
    // GET USER BY ID
    // =========================

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        return userService.toResponseDto(user);
    }

    // =========================
    // CHANGE USER ROLE
    // =========================

    @Transactional
    public UserResponseDto updateUserRole(
            Long id,
            Role role) {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Role cannot be null"
            );
        }

        User targetUser = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );

        // Get currently authenticated admin
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUsername = authentication.getName();

        User currentUser = userRepository
                .findByUsername(currentUsername)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );

        // Prevent admin from changing their own role
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException(
                    "You cannot change your own role"
            );
        }

        targetUser.setRole(role);

        User updatedUser =
                userRepository.save(targetUser);

        return userService.toResponseDto(updatedUser);
    }
}