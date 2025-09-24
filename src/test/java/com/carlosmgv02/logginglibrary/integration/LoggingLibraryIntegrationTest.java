package com.carlosmgv02.logginglibrary.integration;

import com.carlosmgv02.logginglibrary.CustomLogger;
import com.carlosmgv02.logginglibrary.application.service.LoggingApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource(properties = {
        "logging.library.enabled=true",
        "logging.library.metrics-enabled=true",
        "logging.library.trace-enabled=true",
        "logging.sensitive-data.enabled=true"
})
class LoggingLibraryIntegrationTest {

    @Autowired
    private LoggingApplicationService loggingApplicationService;

    @Test
    void contextLoads() {
        assertThat(loggingApplicationService).isNotNull();
    }

    @Test
    void shouldLogInfoMessage() {
        assertDoesNotThrow(() -> {
            loggingApplicationService.info("Test info message from integration test");
        });
    }

    @Test
    void shouldLogErrorWithException() {
        Exception testException = new RuntimeException("Test exception");
        assertDoesNotThrow(() -> {
            loggingApplicationService.error("Test error message with exception", testException);
        });
    }

    @Test
    void shouldLogAllLevels() {
        assertDoesNotThrow(() -> {
            loggingApplicationService.trace("Trace message");
            loggingApplicationService.debug("Debug message");
            loggingApplicationService.info("Info message");
            loggingApplicationService.warn("Warn message");
            loggingApplicationService.error("Error message");
        });
    }

    @Test
    void customLoggerShouldWork() {
        assertDoesNotThrow(() -> {
            CustomLogger.info("Info through CustomLogger");
            CustomLogger.warn("Warning through CustomLogger");
            CustomLogger.error("Error through CustomLogger", new RuntimeException("Test"));
        });
    }

    @Test
    void shouldHandleNullMessages() {
        assertDoesNotThrow(() -> {
            loggingApplicationService.info(null);
        });
    }
}