package com.webauditor.backend.config;

import com.webauditor.backend.entity.AdminRole;
import com.webauditor.backend.entity.AdminUser;
import com.webauditor.backend.repository.AdminUserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

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

                log.warn("Initial admin environment variables are missing; no admin was created.");

                return;
            }


            /*
             * ADMIN ALREADY EXISTS
             */
            var existingAdmin =
                adminUserRepository
                    .findByUsernameIgnoreCase(username);


            if (existingAdmin.isPresent()) {

                log.info("Initial admin account already exists.");


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


            log.info("Initial admin account created.");
        };
    }
}
