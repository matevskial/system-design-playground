package com.matevskial.systemdesignplayground.urlshortener.spring.jdbc;

import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContext;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationContextManager;
import com.matevskial.systemdesignplayground.urlshortener.framework.application.ApplicationException;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Connection;

public class SpringJdbcApplicationContextManager implements ApplicationContextManager {

    @Override
    public void manage(ApplicationContext context) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:6301/url_shortener");
        dataSource.setUser("root");
        dataSource.setPassword("12345678");
        dataSource.setConnectTimeout(200);
        try(Connection c = dataSource.getConnection()) {
            // ignore
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcClient jdbcClient = JdbcClient.create(jdbcTemplate);
        context.registerBean(jdbcTemplate, JdbcTemplate.class);
        context.registerBean(jdbcClient, JdbcClient.class);

        JdbcTransactionManager transactionManager = new JdbcTransactionManager(dataSource);
        context.registerBean(transactionManager, PlatformTransactionManager.class);
    }
}
