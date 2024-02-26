package com.github.victorximenis.multitenant.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class TenantApplicationConfiguration {

    @Value("${multi-tenant.application.name}")
    private String applicationName;

    @Value("${multi-tenant.security.encryption-key}")
    private String encryptionKey;

    @Value("${multi-tenant.datasource-on-demand.enabled}")
    private boolean dataSourceOnDemand;

}
