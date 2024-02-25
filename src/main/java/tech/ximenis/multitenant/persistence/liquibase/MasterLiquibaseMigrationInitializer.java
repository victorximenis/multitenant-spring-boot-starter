package tech.ximenis.multitenant.persistence.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
public class MasterLiquibaseMigrationInitializer implements InitializingBean {

    String liquibaseMainChangeLog = "classpath:db/changelog/db-master.xml";

    @Autowired
    private DataSource masterDataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        runLiquibaseMain();
    }

    public void runLiquibaseMain() throws LiquibaseException {

        SpringLiquibase liquibase = genLiquibase(masterDataSource, liquibaseMainChangeLog);

        clearLocksAndSums(masterDataSource);

        liquibase.afterPropertiesSet();

    }

    public SpringLiquibase genLiquibase(DataSource dataSource, String changeLog) {

        SpringLiquibase liquibase = new SpringLiquibase();
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
            st.executeUpdate("DELETE FROM DATABASECHANGELOGLOCK");

            log.info("[TENANT] - Clear liquibase checksums");
            st.executeUpdate("UPDATE DATABASECHANGELOG SET MD5SUM=NULL");

            con.commit();

        } catch (Exception e) {
            log.error("[TENANT] - Problem while trying to release locks.");
        }

    }

}
