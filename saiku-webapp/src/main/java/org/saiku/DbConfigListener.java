package org.saiku;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.ClientDriver;

/**
 * @author Octavian Ciubotaru
 */
public class DbConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(ClientDriver.class.getName());
        dataSource.setUrl("jdbc:derby://localhost//derby/ccrs");
        dataSource.setUsername("app");
        dataSource.setPassword("app");

        DataSource proxyDataSource = ProxyDataSourceBuilder
                .create(dataSource)
                .queryTransformer(new DerbyOptimizerHintQueryTransformer())
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
