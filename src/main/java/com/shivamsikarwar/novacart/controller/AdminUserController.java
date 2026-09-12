package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.RoleUpdateRequestDto;
import com.shivamsikarwar.novacart.dto.UserResponseDto;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @RequestParam(required = false) Role role,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        if (role != null) {
            return ResponseEntity.ok(
                    adminUserService.getUsersByRole(
                            role,
                            pageable
                    )
            );
        }

        return ResponseEntity.ok(
                adminUserService.getAllUsers(pageable)
        );
    }

    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminUserService.getUserById(id)
        );
    }

    // =========================
    // UPDATE USER ROLE
    // =========================

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequestDto request) {

        return ResponseEntity.ok(
                adminUserService.updateUserRole(
                        id,
                        request.getRole()
                )
        );
    }
}