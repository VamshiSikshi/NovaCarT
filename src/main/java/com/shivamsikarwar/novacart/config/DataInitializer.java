package com.shivamsikarwar.novacart.config;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("VamshiSikshi");
                admin.setEmail("warshi9890@gmail.com");
                admin.setPassword(
                        passwordEncoder.encode("Sikshi@9123n")
                );

                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                System.out.println("Admin user created.");
            }
        };
    }
}
