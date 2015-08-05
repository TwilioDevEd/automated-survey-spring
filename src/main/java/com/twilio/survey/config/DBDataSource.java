package com.twilio.survey.config;

import com.twilio.survey.util.AppSetup;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

@Configuration public class DBDataSource {
    @ConfigurationProperties(prefix = "spring.datasource") @Bean(name = "dBDataSource") @Primary
    public DataSource dBdataSource() {
        AppSetup appSetup = new AppSetup();
        Map<String, String> params = appSetup.getParamsFromDBURL(appSetup.getDatabaseURL());

        return DataSourceBuilder.create().username(params.get("username")).password(params.get("url"))
            .url(params.get("url")).type(PGPoolingDataSource.class)
            .driverClassName("org.postgresql.Driver").build();
    }

//    @Bean(name = "jdbcSource")
//    public JdbcTemplate jdbcTemplate(DataSource dBDataSource) {
//        return new JdbcTemplate(dBDataSource);
//    }
}
