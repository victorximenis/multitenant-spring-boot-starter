package tech.ximenis.multitenant.persistence.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "multi-tenant.tenant.enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaRepositories(
        basePackages = { "${multi-tenant.tenant.repository.packages}" },
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
@EnableConfigurationProperties(JpaProperties.class)
public class TenantPersistenceConfig {
    private final ConfigurableListableBeanFactory beanFactory;
    private final JpaProperties jpaProperties;
    private final String entityPackages;

    @Autowired
    public TenantPersistenceConfig(
            ConfigurableListableBeanFactory beanFactory,
            JpaProperties jpaProperties,
            @Value("${multi-tenant.tenant.entityManager.packages}")
            String entityPackages) {
        this.beanFactory = beanFactory;
        this.jpaProperties = jpaProperties;
        this.entityPackages = entityPackages;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            @Qualifier("tenantDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setPersistenceUnitName("tenant-persistence-unit");
        em.setPackagesToScan(entityPackages);
        em.setDataSource(dataSource);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public JpaTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager tenantTransactionManager = new JpaTransactionManager();
        tenantTransactionManager.setEntityManagerFactory(emf);
        return tenantTransactionManager;
    }
}
