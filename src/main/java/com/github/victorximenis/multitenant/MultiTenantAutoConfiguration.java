package com.github.victorximenis.multitenant;

import com.github.victorximenis.multitenant.config.TenantApplicationConfiguration;
import com.github.victorximenis.multitenant.service.TenantService;
import com.github.victorximenis.multitenant.service.impl.TenantServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.github.victorximenis.multitenant.persistence"
})
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
