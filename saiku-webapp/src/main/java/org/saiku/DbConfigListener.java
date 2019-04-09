package org.saiku;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author Octavian Ciubotaru
 */
public class DbConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String url = System.getenv("SPRING_DATASOURCE_URL");
        String user = System.getenv("SPRING_DATASOURCE_USERNAME");
        String pass = System.getenv("SPRING_DATASOURCE_PASSWORD");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);

        // Enable useOldAliasMetadataBehavior to return column alias in all cases, otherwise mondrian will fail.
        // Enable useMysqlMetadata to return MySQL product name, to help mondrian select the correct db dialect.
        // MariaDB dialect was added in mondrian 7.2.x
        dataSource.setConnectionProperties("useOldAliasMetadataBehavior=true;useMysqlMetadata=true");

        DataSource proxyDataSource = ProxyDataSourceBuilder
                .create(dataSource)
                .build();

        try {
            InitialContext ic = new InitialContext();
            ic.bind("mondrianDataSource", proxyDataSource);
        } catch (NamingException e) {
            throw new RuntimeException("Failed to bind data source.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            InitialContext ic = new InitialContext();
            ic.unbind("mondrianDataSource");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to unbind data source.", e);
        }
    }
}
