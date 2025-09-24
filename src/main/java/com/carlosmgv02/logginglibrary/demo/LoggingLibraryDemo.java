package com.carlosmgv02.logginglibrary.demo;

import com.carlosmgv02.logginglibrary.CustomLogger;
import com.carlosmgv02.logginglibrary.application.service.LoggingApplicationService;
import com.carlosmgv02.logginglibrary.application.usecase.LogEntryUseCase;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "logging.demo.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class LoggingLibraryDemo implements CommandLineRunner {

    private final LoggingApplicationService loggingApplicationService;
    private final LogEntryUseCase logEntryUseCase;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Logging Library Demo...");

        demonstrateBasicLogging();
        demonstrateCustomLogger();
        demonstrateMetadataLogging();
        demonstrateExceptionLogging();
        demonstrateSensitiveDataFiltering();

        log.info("Logging Library Demo completed!");
    }

    private void demonstrateBasicLogging() {
        log.info("=== Basic Logging Demo ===");

        loggingApplicationService.trace("This is a trace message");
        loggingApplicationService.debug("This is a debug message");
        loggingApplicationService.info("This is an info message");
        loggingApplicationService.warn("This is a warning message");
        loggingApplicationService.error("This is an error message");
    }

    private void demonstrateCustomLogger() {
        log.info("=== Custom Logger Demo ===");

        CustomLogger.info("Using CustomLogger - Info message");
        CustomLogger.warn("Using CustomLogger - Warning message");
        CustomLogger.error("Using CustomLogger - Error message");
    }

    private void demonstrateMetadataLogging() {
        log.info("=== Metadata Logging Demo ===");

        Map<String, Object> metadata = Map.of(
                "userId", "user123",
                "requestId", "req456",
                "operation", "userCreation",
                "duration", 1250L
        );

        logEntryUseCase.logWithMetadata(
                "User creation completed successfully",
                LogLevel.INFO,
                metadata
        );
    }

    private void demonstrateExceptionLogging() {
        log.info("=== Exception Logging Demo ===");

        try {
            // Simulate an exception
            throw new IllegalArgumentException("This is a simulated exception for demo purposes");
        } catch (Exception e) {
            loggingApplicationService.error("An exception occurred during demo", e);
            CustomLogger.error("Exception caught by CustomLogger", e);
        }
    }

    private void demonstrateSensitiveDataFiltering() {
        log.info("=== Sensitive Data Filtering Demo ===");

        // These should be filtered by the SensitiveDataLogFilter
        loggingApplicationService.info("User attempted login with password=mysecretpassword");
        loggingApplicationService.info("API request with token: abc123def456ghi789");
        loggingApplicationService.info("Processing payment for credit card: 4532123456789012");

        // This should not be filtered
        loggingApplicationService.info("Normal log message without sensitive information");
    }
}