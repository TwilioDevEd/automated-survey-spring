package com.twilio.survey.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.twilio.survey.repositories")
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.twilio.survey.models"})
public class RepositoryConfig {
}
