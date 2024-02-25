package tech.ximenis.multitenant.persistence.liquibase;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(MasterLiquibaseMigrationInitializer.class)
@ConditionalOnProperty(name = "multi-tenant.tenant.enabled", havingValue = "true", matchIfMissing = true)
public class TenantLiquibaseConfig {

    @Bean
    @ConditionalOnProperty(name = "multi-tenan.tenant.liquibase.enabled", havingValue = "true", matchIfMissing = true)
    TenantLiquibaseMigration tenantLiquibaseMigration() {
        return new TenantLiquibaseMigration();
    }

}
