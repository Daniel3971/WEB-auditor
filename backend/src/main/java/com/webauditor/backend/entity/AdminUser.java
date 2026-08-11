package com.webauditor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "admin_users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_admin_username",
            columnNames = "username"
        ),
        @UniqueConstraint(
            name = "uk_admin_email",
            columnNames = "email"
        )
    }
)
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
        nullable = false,
        length = 100
    )
    private String username;


    @Column(
        nullable = false,
        length = 150
    )
    private String email;


    @Column(
        name = "password_hash",
        nullable = false,
        length = 255
    )
    private String passwordHash;


    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 30
    )
    private AdminRole role =
        AdminRole.ADMIN;


    @Column(
        nullable = false
    )
    private boolean enabled = true;


    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(
        String username
    ) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(
        String email
    ) {
        this.email = email;
    }


    public String getPasswordHash() {
        return passwordHash;
    }


    public void setPasswordHash(
        String passwordHash
    ) {
        this.passwordHash =
            passwordHash;
    }


    public AdminRole getRole() {
        return role;
    }


    public void setRole(
        AdminRole role
    ) {
        this.role = role;
    }


    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(
        boolean enabled
    ) {
        this.enabled = enabled;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}