package com.webauditor.backend.config;

import com.webauditor.backend.entity.AdminRole;
import com.webauditor.backend.entity.AdminUser;
import com.webauditor.backend.repository.AdminUserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
        AdminUserRepository adminUserRepository,
        PasswordEncoder passwordEncoder
    ) {

        return args -> {

            String username =
                System.getenv("ADMIN_USERNAME");

            String email =
                System.getenv("ADMIN_EMAIL");

            String password =
                System.getenv("ADMIN_PASSWORD");


            if (
                username == null ||
                email == null ||
                password == null
            ) {

                System.out.println(
                    "ADMIN ENVIRONMENT VARIABLES ARE MISSING"
                );

                return;
            }


            /*
             * ADMIN ALREADY EXISTS
             */
            var existingAdmin =
                adminUserRepository
                    .findByUsernameIgnoreCase(username);


            if (existingAdmin.isPresent()) {

                AdminUser admin =
                    existingAdmin.get();


                boolean passwordMatches =
                    passwordEncoder.matches(
                        password,
                        admin.getPasswordHash()
                    );


                System.out.println(
                    "================================"
                );

                System.out.println(
                    "ADMIN USER FOUND: "
                    + admin.getUsername()
                );

                System.out.println(
                    "ADMIN ENABLED: "
                    + admin.isEnabled()
                );

                System.out.println(
                    "ADMIN PASSWORD MATCHES: "
                    + passwordMatches
                );

                System.out.println(
                    "================================"
                );


                return;
            }


            /*
             * CREATE ADMIN
             */

            AdminUser admin =
                new AdminUser();


            admin.setUsername(
                username
            );


            admin.setEmail(
                email
            );


            admin.setPasswordHash(
                passwordEncoder.encode(
                    password
                )
            );


            admin.setRole(
                AdminRole.SUPER_ADMIN
            );


            admin.setEnabled(
                true
            );


            adminUserRepository.save(
                admin
            );


            System.out.println(
                "================================"
            );

            System.out.println(
                "INITIAL ADMIN CREATED"
            );

            System.out.println(
                "USERNAME: "
                + username
            );

            System.out.println(
                "================================"
            );
        };
    }
}