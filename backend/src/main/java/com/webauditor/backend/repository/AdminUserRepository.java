package com.webauditor.backend.repository;

import com.webauditor.backend.entity.AdminUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository
        extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser>
        findByUsernameIgnoreCase(
            String username
        );

    Optional<AdminUser>
        findByEmailIgnoreCase(
            String email
        );

    boolean existsByUsernameIgnoreCase(
        String username
    );

    boolean existsByEmailIgnoreCase(
        String email
    );
}