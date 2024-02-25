package tech.ximenis.multitenant;

import tech.ximenis.multitenant.config.TenantApplicationConfiguration;
import tech.ximenis.multitenant.service.TenantService;
import tech.ximenis.multitenant.service.impl.TenantServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantAutoConfiguration {

    @Bean
    public TenantFilter tenantFilter(){
        return new TenantFilter();
    }

    @Bean
    @ConditionalOnProperty(name = "multi-tenant.enabled", havingValue = "true", matchIfMissing = true)
    public TenantService tenantService(){
        return new TenantServiceImpl();
    }

    @Bean
    public TenantApplicationConfiguration tenantApplicationConfiguration(){
        return new TenantApplicationConfiguration();
    }

}
