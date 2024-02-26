package com.github.victorximenis.multitenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;

public class LiquibaseExclusionEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final String PROPERTY_NAME = "spring.autoconfigure.exclude";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String existingExcludes = environment.getProperty(PROPERTY_NAME);
        String liquibaseAutoConfig = "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration";
        if (existingExcludes == null) {
            environment.getPropertySources().addFirst(new MapPropertySource("springMtaStarterExclusions",
                    Collections.singletonMap(PROPERTY_NAME, liquibaseAutoConfig)));
        } else if (!existingExcludes.contains(liquibaseAutoConfig)) {
            environment.getPropertySources().addFirst(new MapPropertySource("springMtaStarterExclusions",
                    Collections.singletonMap(PROPERTY_NAME, existingExcludes + "," + liquibaseAutoConfig)));
        }
    }
}
