package com.payroll.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:postgresql://localhost:5432/payroll_db");
            ds.setUsername("payroll_user");
            ds.setPassword("payroll_pass");
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setMaximumPoolSize(10);
            ds.setMinimumIdle(5);
            return ds;
        }

        URI uri = URI.create(databaseUrl);
        String userInfo = uri.getUserInfo();
        String username = userInfo.split(":")[0];
        String password = userInfo.split(":")[1];
        String host = uri.getHost();
        int port = uri.getPort();
        String database = uri.getPath().substring(1);
        String query = uri.getQuery();

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        if (query != null && !query.isEmpty()) {
            jdbcUrl += "?" + query;
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaximumPoolSize(10);
        ds.setMinimumIdle(5);
        ds.setDriverClassName("org.postgresql.Driver");
        return ds;
    }
}
