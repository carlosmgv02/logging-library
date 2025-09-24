package com.carlosmgv02.logginglibrary.config;

import com.carlosmgv02.logginglibrary.infrastructure.adapter.SensitiveDataProperties;
import com.carlosmgv02.logginglibrary.infrastructure.config.LoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.carlosmgv02.logginglibrary")
@EnableConfigurationProperties({LoggingProperties.class, SensitiveDataProperties.class})
@Slf4j
public class LoggingLibraryAutoConfiguration {

    public LoggingLibraryAutoConfiguration() {
        log.info("Logging Library initialized");
    }
}