package com.twilio.survey.config;

import com.twilio.survey.util.AppSetup;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = "com.twilio.survey.repositories", entityManagerFactoryRef = "entityManagerFactory")
public class DBDataSource {
    @Autowired
    private JpaVendorAdapter jpaVendorAdapter;

    /**
     * Here the data source is defined. It uses AppSetup class to get the database url that
     * includes username and password
     *
     * @return DataSource
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean(name = "dBDataSource")
    @Primary
    public DataSource dBDataSource() {
        AppSetup appSetup = new AppSetup();
        Map<String, String> params = appSetup.getParamsFromDBURL(appSetup.getDatabaseURL());

        return DataSourceBuilder.create().url(params.get("url")).type(PGPoolingDataSource.class)
                .driverClassName("org.postgresql.Driver").build();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dBEntityManager() throws Throwable {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dBDataSource());

        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setPackagesToScan("com.twilio.survey.models");
        return emfb;
    }
}
