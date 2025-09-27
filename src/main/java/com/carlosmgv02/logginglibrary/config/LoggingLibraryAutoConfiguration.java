package com.carlosmgv02.logginglibrary.config;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import com.carlosmgv02.logginglibrary.infrastructure.adapter.SensitiveDataProperties;
import com.carlosmgv02.logginglibrary.infrastructure.config.HttpTracingProperties;
import com.carlosmgv02.logginglibrary.infrastructure.config.LoggingProperties;
import com.carlosmgv02.logginglibrary.infrastructure.web.TracingHttpFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.carlosmgv02.logginglibrary")
@EnableConfigurationProperties({LoggingProperties.class, SensitiveDataProperties.class, HttpTracingProperties.class})
@Slf4j
public class LoggingLibraryAutoConfiguration {

    public LoggingLibraryAutoConfiguration() {
        log.info("Logging Library initialized");
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnProperty(name = "logging.http.tracing.enabled", havingValue = "true", matchIfMissing = true)
    static class HttpTracingConfiguration {

        public HttpTracingConfiguration() {
            log.info("HTTP Tracing Filter enabled");
        }

        @Bean
        public TracingHttpFilter tracingHttpFilter(TraceContextProvider traceContextProvider) {
            return new TracingHttpFilter(traceContextProvider);
        }
    }
}