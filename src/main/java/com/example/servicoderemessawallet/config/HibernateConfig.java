package com.example.servicoderemessawallet.config;

import org.hibernate.boot.model.TypeContributor;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(List<TypeContributor> typeContributors) {
        return hibernateProperties -> {
            if (hibernateProperties.get("hibernate.type_contributors") instanceof EntityManagerFactoryBuilderImpl) {
                hibernateProperties.put("hibernate.type_contributors", typeContributors);
            }
        };
    }
}