package com.github.victorximenis.multitenant.persistence.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@ConditionalOnBean(name = "masterDataSource")
@EnableJpaRepositories(
        basePackages = { "com.github.victorximenis.multitenant.persistence" },
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager"
)
@EnableConfigurationProperties({DataSourceProperties.class, JpaProperties.class})
public class MasterPersistenceConfig {
    private final ConfigurableListableBeanFactory beanFactory;
    private final JpaProperties jpaProperties;
    private final String entityPackages;

    @Autowired
    public MasterPersistenceConfig(ConfigurableListableBeanFactory beanFactory,
                                   JpaProperties jpaProperties,
                                   @Value("com.github.victorximenis.multitenant.model")
                                   String entityPackages) {
        this.beanFactory = beanFactory;
        this.jpaProperties = jpaProperties;
        this.entityPackages = entityPackages;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(
            @Qualifier("masterDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setPersistenceUnitName("master-persistence-unit");
        em.setPackagesToScan(entityPackages);
        em.setDataSource(dataSource);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public JpaTransactionManager masterTransactionManager(
            @Qualifier("masterEntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
