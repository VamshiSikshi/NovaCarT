package com.shivamsikarwar.novacart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AutoConfig {

    @Value("${audit.username}")
    private String auditorUsername;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(auditorUsername);
    }
}