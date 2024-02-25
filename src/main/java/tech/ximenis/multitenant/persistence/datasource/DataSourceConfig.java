package tech.ximenis.multitenant.persistence.datasource;

import tech.ximenis.multitenant.config.TenantApplicationConfiguration;
import tech.ximenis.multitenant.persistence.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "multi-tenant.enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig masterHikariConfig(){
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("multi-tenant.datasource.hikari")
    public HikariConfig tenantHikariConfig(){
        return new HikariConfig();
    }

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties masterDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        DataSourceProperties dataSourceProperties = masterDataSourceProperties();
        HikariConfig config = masterHikariConfig();
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());
        config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setPoolName("masterDataSource");
        return new HikariDataSource(config);
    }

    @Bean(name = "tenantDataSource")
    @ConditionalOnProperty(name = "multi-tenant.enabled", havingValue = "true", matchIfMissing = true)
    public DataSource dataSource(TenantApplicationConfiguration tenantApplicationConfiguration,
                                 TenantRepository tenantRepository,
                                 MeterRegistry meterRegistry,
                                 ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {

        MultiTenantDatasource multiTenantDataSource = new MultiTenantDatasource(
                tenantApplicationConfiguration
                , tenantRepository
                , tenantHikariConfig()
                , meterRegistry
                , metadataProviders
        );

        multiTenantDataSource.setTargetDataSources(new ConcurrentHashMap<Object, Object>());
        multiTenantDataSource.setDefaultTargetDataSource(masterDataSource());

        try {
            tenantRepository.findAll().forEach(tenant->{
                multiTenantDataSource.addDataSource(tenant);
            });
        } catch (Exception ex) {
            log.warn(String.format("DataSource - No Tenants: %s", ex.getMessage()));
        }

        return multiTenantDataSource;
    }

}
