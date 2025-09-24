package com.carlosmgv02.logginglibrary.infrastructure.validation;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.status.Status;
import com.carlosmgv02.logginglibrary.infrastructure.config.LoggingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Validates the logging system configuration and connectivity during application startup.
 *
 * <p>This validator performs comprehensive checks on the logging infrastructure including:
 * <ul>
 *   <li>Appender status verification</li>
 *   <li>External system connectivity (e.g., Logstash, ELK stack)</li>
 *   <li>Configuration error detection</li>
 *   <li>Performance impact assessment</li>
 * </ul>
 *
 * <p>The validation behavior can be customized through configuration properties:
 * <pre>
 * logging:
 *   library:
 *     validation:
 *       enabled: true
 *       strict-mode: false
 *       fail-on-logstash-connection-error: false
 *       validation-delay-ms: 2000
 * </pre>
 *
 * @author Carlos MGV
 * @since 0.0.2
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "logging.library.validation", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingSystemValidator {

    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(1);
    private static final Set<String> CRITICAL_ERROR_KEYWORDS = Set.of(
        "connection failed", "Connection refused", "unreachable", "timeout"
    );

    private final LoggingProperties loggingProperties;

    @PostConstruct
    public void validateLoggingSystemOnStartup() {
        if (!isValidationEnabled()) {
            return;
        }

        awaitAppenderInitialization();

        try {
            ValidationResult result = performValidation();
            handleValidationResult(result);
        } catch (Exception e) {
            handleValidationFailure(e);
        }
    }

    /**
     * Performs comprehensive logging system validation.
     *
     * @return ValidationResult containing all validation findings
     */
    private ValidationResult performValidation() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);

        List<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenders =
            StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(rootLogger.iteratorForAppenders(), Spliterator.ORDERED),
                false
            ).toList();

        return ValidationResult.builder()
            .totalAppenders(appenders.size())
            .startedAppenders((int) appenders.stream().filter(Appender::isStarted).count())
            .logstashConnectivity(assessLogstashConnectivity(appenders))
            .systemErrors(countSystemErrors(context))
            .issues(collectIssues(appenders))
            .build();
    }

    private boolean isValidationEnabled() {
        return loggingProperties.getValidation().isEnabled();
    }

    private void awaitAppenderInitialization() {
        int delayMs = loggingProperties.getValidation().getValidationDelayMs();
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("Validation delay interrupted");
            }
        }
    }

    private LogstashConnectivity assessLogstashConnectivity(List<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenders) {
        Optional<LogstashTcpSocketAppender> logstashAppender = appenders.stream()
            .map(this::findLogstashAppender)
            .filter(Objects::nonNull)
            .findFirst();

        return logstashAppender.map(logstashTcpSocketAppender -> testLogstashConnectivity(logstashTcpSocketAppender)
                                                                         ? LogstashConnectivity.CONNECTED
                                                                         : LogstashConnectivity.UNREACHABLE).orElse(LogstashConnectivity.NOT_CONFIGURED);

    }

    private long countSystemErrors(LoggerContext context) {
        return context.getStatusManager().getCopyOfStatusList().stream()
            .filter(status -> status.getLevel() == Status.ERROR)
            .count();
    }

    private List<String> collectIssues(List<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> appenders) {
        List<String> issues = new ArrayList<>();

        appenders.stream()
            .filter(appender -> !appender.isStarted())
            .forEach(appender -> issues.add("Appender '" + appender.getName() + "' not started"));

        return issues;
    }

    private void handleValidationResult(ValidationResult result) {
        LoggingProperties.ValidationProperties config = loggingProperties.getValidation();

        if (result.startedAppenders() == 0) {
            throw new LoggingValidationException("No appenders started");
        }

        if (result.systemErrors() > 0) {
            throw new LoggingValidationException(result.systemErrors() + " system errors found");
        }

        if (result.logstashConnectivity() == LogstashConnectivity.UNREACHABLE &&
            config.isFailOnLogstashConnectionError()) {
            throw new LoggingValidationException("Logstash connection failed");
        }

        logValidationStatus(result);
    }

    private void logValidationStatus(ValidationResult result) {
        List<String> allIssues = new ArrayList<>(result.issues());

        if (result.logstashConnectivity() == LogstashConnectivity.UNREACHABLE) {
            allIssues.add("Logstash unreachable");
        }

        if (result.systemErrors() > 0) {
            allIssues.add(result.systemErrors() + " logging errors");
        }

        if (!allIssues.isEmpty()) {
            log.warn("Logging system: OK with warnings - {}", String.join(", ", allIssues));
        } else {
            String connectivityStatus = switch (result.logstashConnectivity()) {
                case CONNECTED -> "Logstash connected";
                case UNREACHABLE -> "Logstash unreachable";
                case NOT_CONFIGURED -> "Local only";
            };
            log.info("Logging system: OK - {} appenders active, {}",
                result.startedAppenders(), connectivityStatus);
        }
    }

    private void handleValidationFailure(Exception e) {
        if (e instanceof LoggingValidationException) {
            log.error("Logging system: FAILED - {}", e.getMessage());
        } else {
            log.error("Logging system: FAILED - Validation error: {}", e.getMessage());
        }
        throw new RuntimeException("Logging system validation failed", e);
    }

    private LogstashTcpSocketAppender findLogstashAppender(Appender<ch.qos.logback.classic.spi.ILoggingEvent> appender) {
        // Direct LogstashTcpSocketAppender
        if (appender instanceof LogstashTcpSocketAppender) {
            return (LogstashTcpSocketAppender) appender;
        }

        // Look inside AsyncAppender
        if (appender instanceof AsyncAppender asyncAppender) {
            Iterator<Appender<ch.qos.logback.classic.spi.ILoggingEvent>> iterator = asyncAppender.iteratorForAppenders();
            while (iterator.hasNext()) {
                Appender<ch.qos.logback.classic.spi.ILoggingEvent> innerAppender = iterator.next();
                if (innerAppender instanceof LogstashTcpSocketAppender) {
                    return (LogstashTcpSocketAppender) innerAppender;
                }
            }
        }

        return null;
    }

    private boolean testLogstashConnectivity(LogstashTcpSocketAppender appender) {
        try {
            // Extract the destination from the appender
            String destination = appender.getDestinations().get(0).toString();

            // Parse host and port from destination (format: "host:port")
            String[] parts = destination.split(":");
            if (parts.length != 2) {
                log.debug("Could not parse Logstash destination: {}", destination);
                return false;
            }

            String host = parts[0];
            int port;
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                log.debug("Invalid port in Logstash destination: {}", destination);
                return false;
            }

            // Try to establish a connection with a short timeout
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 1000); // 1 second timeout
                return true;
            }
        } catch (IOException e) {
            // Connection failed - this is expected if Logstash is not running
            return false;
        } catch (Exception e) {
            log.debug("Error testing Logstash connectivity: {}", e.getMessage());
            return false;
        }
    }
}