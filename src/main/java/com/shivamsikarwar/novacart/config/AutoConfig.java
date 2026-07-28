package com.shivamsikarwar.novacart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AutoConfig {
    @Bean
    public AuditorAware<String> auditorProvider(){
        return ()-> Optional.of("admin@example.com");
    }
}
