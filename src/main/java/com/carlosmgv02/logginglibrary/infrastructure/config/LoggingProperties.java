package com.carlosmgv02.logginglibrary.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logging.library")
public class LoggingProperties {
    private boolean enabled = true;
    private boolean metricsEnabled = true;
    private boolean traceEnabled = true;
    private String serviceName;
    private int bufferSize = 1000;
    private long flushIntervalMs = 5000L;
    private String logLevel = "INFO";

    // Startup validation properties
    private ValidationProperties validation = new ValidationProperties();

    @Data
    public static class ValidationProperties {
        private boolean enabled = true;
        private boolean strictMode = false;
        private boolean failOnLogstashConnectionError = false;
        private boolean failOnConnectionWarnings = false;
        private int validationDelayMs = 2000;
    }
}