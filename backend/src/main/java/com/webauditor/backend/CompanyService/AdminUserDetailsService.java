package com.webauditor.backend.CompanyService;

import com.webauditor.backend.entity.AdminUser;
import com.webauditor.backend.repository.AdminUserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService
        implements UserDetailsService {

    private final AdminUserRepository
        adminUserRepository;


    public AdminUserDetailsService(
        AdminUserRepository adminUserRepository
    ) {
        this.adminUserRepository =
            adminUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(
        String username
    ) throws UsernameNotFoundException {

        AdminUser admin =
            adminUserRepository
                .findByUsernameIgnoreCase(
                    username
                )
                .orElseThrow(
                    () ->
                        new UsernameNotFoundException(
                            "Admin user not found."
                        )
                );


        return User
            .withUsername(
                admin.getUsername()
            )
            .password(
                admin.getPasswordHash()
            )
            .roles(
                admin.getRole().name()
            )
            .disabled(
                !admin.isEnabled()
            )
            .build();
    }
}