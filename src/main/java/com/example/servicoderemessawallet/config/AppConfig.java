package com.example.servicoderemessawallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public UUIDTypeContributor uuidTypeContributor() {
        return new UUIDTypeContributor();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
