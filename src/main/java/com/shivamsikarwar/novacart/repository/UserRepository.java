package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);

    Page<User> findByRoleOrderByIdDesc(
            Role role,
            Pageable pageable
    );

    long countByRole(Role role);
}