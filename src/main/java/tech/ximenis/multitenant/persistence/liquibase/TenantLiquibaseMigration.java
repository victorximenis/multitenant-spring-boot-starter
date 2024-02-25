package tech.ximenis.multitenant.persistence.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import tech.ximenis.multitenant.model.Tenant;
import tech.ximenis.multitenant.persistence.TenantRepository;
import tech.ximenis.multitenant.util.AESUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collection;

@Slf4j
public class TenantLiquibaseMigration {

    String liquibaseMainChangeLog = "classpath:db/changelog/db-tenant.xml";

    @Value("${multi-tenant.security.encryption-key}")
    private String encryptionKey;

    @Value("${multi-tenant.application.name}")
    private String applicationName;

    @Autowired
    private TenantRepository tenantRepository;

    public void migrateAllTenants(Collection<Tenant> tenants) {
        for(Tenant t : tenants){
            this.migrateTenant(t);
        }
    }

    public void migrateTenant(Tenant tenant) {

        try (Connection connection = DriverManager.getConnection(tenant.getJdbcUrl(), tenant.getUsername(), AESUtils.decrypt(encryptionKey, tenant.getPassword()))){

            DataSource tenantDataSource = new SingleConnectionDataSource(connection, false);

            SpringLiquibase liquibase = genLiquibase(tenantDataSource, liquibaseMainChangeLog);

            clearLocksAndSums(tenantDataSource);

            liquibase.afterPropertiesSet();

        }catch (Exception e ){
            log.error("Failed to run Liquibase migrations for tenant " + tenant.getTenantId(), e.getMessage());
        }
    }

    public SpringLiquibase genLiquibase(DataSource dataSource, String changeLog) {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDatabaseChangeLogTable("msbs_tenant_changelog");
        liquibase.setDatabaseChangeLogLockTable("msbs_tenant_changeloglock");
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(true);
        liquibase.setResourceLoader(new DefaultResourceLoader(getClass().getClassLoader()));

        return liquibase;

    }

    public void clearLocksAndSums(DataSource dataSource) {

        try {

            @Cleanup
            Connection con = dataSource.getConnection();
            con.setAutoCommit(false);

            @Cleanup
            Statement st = con.createStatement();

            log.info("[TENANT] - Clear liquibase locks");
            st.executeUpdate("DELETE FROM MSBS_TENANT_CHANGELOGLOCK");

            log.info("[TENANT] - Clear liquibase checksums");
            st.executeUpdate("UPDATE MSBS_TENANT_CHANGELOG SET MD5SUM=NULL");

            con.commit();

        } catch (Exception e) {
            log.error("[TENANT] - Problem while trying to release locks.");
        }

    }

}
