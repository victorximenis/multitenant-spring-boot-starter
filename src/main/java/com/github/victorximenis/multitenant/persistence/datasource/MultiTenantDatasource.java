package com.github.victorximenis.multitenant.persistence.datasource;

import com.github.victorximenis.multitenant.TenantContext;
import com.github.victorximenis.multitenant.config.TenantApplicationConfiguration;
import com.github.victorximenis.multitenant.model.Tenant;
import com.github.victorximenis.multitenant.persistence.TenantRepository;
import com.github.victorximenis.multitenant.util.AESUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.metrics.jdbc.DataSourcePoolMetrics;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.core.log.LogMessage;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;
import io.micrometer.core.instrument.MeterRegistry;

@RequiredArgsConstructor
public class MultiTenantDatasource extends AbstractRoutingDataSource {

    private static final String TENANT_POOL_NAME_SUFFIX = "datasource-pool";

    private final TenantApplicationConfiguration tenantApplicationConfiguration;

    private final TenantRepository tenantRepository;
    private final HikariConfig tenantHikariConfig;
    private final MeterRegistry meterRegistry;
    private final ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders;

    private Map<Object, Object> targetDataSources;

    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getTenantId();

        if(tenantApplicationConfiguration.isDataSourceOnDemand() &&
                Objects.nonNull(tenant) &&
                !targetDataSources.containsKey(tenant)) {

            logger.debug(String.format("Database of tenant %s not created!", tenant));

            Optional<Tenant> tenantOpt = tenantRepository.findTenantByTenantId(tenant);

            if(tenantOpt.isPresent()){
                addDataSource(tenantOpt.get());
            } else {
                logger.debug(String.format("Unable to find tenant %s on database!", tenant));
            }
        }

        return tenant;
    }

    public void addDataSource(Tenant tenant){
        try {
            if(tenant.getActive()){
                DataSource dataSource = createDataSource(tenant);
                addTargetDataSource(tenant.getTenantId(), dataSource);
            } else {
                logger.debug(String.format("Inactive tenant %s!", tenant));
            }
        } catch (Exception e){
            logger.error(String.format("Error creating datasource for tenant %s!", tenant.getTenantId()), e);
        }
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }

    public void addTargetDataSource(Object tenant, Object dataSource) {
        targetDataSources.putIfAbsent(tenant, dataSource);
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
    }

    private synchronized DataSource createDataSource(Tenant tenant) {
        HikariConfig config = this.tenantHikariConfig;
        config.setUsername(tenant.getUsername());
        config.setPassword(AESUtils.decrypt(tenantApplicationConfiguration.getEncryptionKey(), tenant.getPassword()));
        config.setJdbcUrl(tenant.getJdbcUrl());
        config.setPoolName(String.format("%s-%s", tenant.getName().toLowerCase(), TENANT_POOL_NAME_SUFFIX));
        config.setMaximumPoolSize(tenant.getMaxPoolSize());
        config.setMinimumIdle(tenant.getMinimumIdle());
        config.setIdleTimeout(tenant.getIdleTimeout());

        HikariDataSource dataSource = new HikariDataSource(config);

        List<DataSourcePoolMetadataProvider> metadataProvidersList = metadataProviders.stream()
                .collect(Collectors.toList());

        bindMetricsRegistryToHikariDataSource(dataSource);
        bindDataSourceToRegistry(dataSource, metadataProvidersList, meterRegistry, tenant.getTenantId());

        return dataSource;
    }

    private void bindMetricsRegistryToHikariDataSource(HikariDataSource hikari) {
        if (hikari.getMetricRegistry() == null && hikari.getMetricsTrackerFactory() == null) {
            try {
                hikari.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(this.meterRegistry));
            }
            catch (Exception ex) {
                logger.warn(LogMessage.format("Failed to bind Hikari metrics: %s", ex.getMessage()));
            }
        }
    }

    private void bindDataSourceToRegistry(DataSource dataSource,
                                          Collection<DataSourcePoolMetadataProvider> metadataProviders,
                                          MeterRegistry registry,
                                          String dataSourceName) {

        new DataSourcePoolMetrics(dataSource, metadataProviders, dataSourceName, Collections.emptyList()).bindTo(registry);
    }

}
