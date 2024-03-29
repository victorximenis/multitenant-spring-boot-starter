package com.github.victorximenis.multitenant.persistence.liquibase;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "multi-tenant.liquibase.enabled", havingValue = "true")
public class MasterLiquibaseConfig {

    @Bean
    MasterLiquibaseMigrationInitializer masterLiquibaseMigrationInitializer(){
        return new MasterLiquibaseMigrationInitializer();
    }

}
