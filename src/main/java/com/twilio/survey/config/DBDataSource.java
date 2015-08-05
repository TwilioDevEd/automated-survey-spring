package com.twilio.survey.config;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration public class DBDataSource {
    @ConfigurationProperties(prefix = "spring.datasource") @Bean @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().username("survey").password("survey")
            .url("jdbc:postgresql://localhost:5432/survey").type(PGPoolingDataSource.class)
            .driverClassName("org.postgresql.Driver").build();
    }
}
