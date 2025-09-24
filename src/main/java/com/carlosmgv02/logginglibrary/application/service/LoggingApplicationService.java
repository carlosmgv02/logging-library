package com.carlosmgv02.logginglibrary.application.service;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import com.carlosmgv02.logginglibrary.domain.port.LogFilter;
import com.carlosmgv02.logginglibrary.domain.port.LogMetricsCollector;
import com.carlosmgv02.logginglibrary.domain.port.LogProcessor;
import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import com.carlosmgv02.logginglibrary.infrastructure.config.LoggingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoggingApplicationService {
    private final List<LogProcessor> logProcessors;
    private final List<LogFilter> logFilters;
    private final TraceContextProvider traceContextProvider;
    private final LogMetricsCollector metricsCollector;
    private final LoggingProperties properties;

    public void trace(String message) {
        trace(message, null);
    }

    public void trace(String message, Throwable throwable) {
        processLog(message, LogLevel.TRACE, throwable, Map.of());
    }

    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable throwable) {
        processLog(message, LogLevel.DEBUG, throwable, Map.of());
    }

    public void info(String message) {
        info(message, null);
    }

    public void info(String message, Throwable throwable) {
        processLog(message, LogLevel.INFO, throwable, Map.of());
    }

    public void warn(String message) {
        warn(message, null);
    }

    public void warn(String message, Throwable throwable) {
        processLog(message, LogLevel.WARN, throwable, Map.of());
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(String message, Throwable throwable) {
        processLog(message, LogLevel.ERROR, throwable, Map.of());
    }

    public void logWithMetadata(String message, LogLevel level, Map<String, Object> metadata) {
        processLog(message, level, null, metadata);
    }

    public void logWithMetadata(String message, LogLevel level, Map<String, Object> metadata, Throwable throwable) {
        processLog(message, level, throwable, metadata);
    }

    private void processLog(String message, LogLevel level, Throwable throwable, Map<String, Object> metadata) {
        if (!properties.isEnabled() || !isLevelEnabled(level)) {
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            LogEntry.LogEntryBuilder entryBuilder = LogEntry.builder()
                    .message(message)
                    .level(level)
                    .logger(getCallerClass())
                    .serviceName(properties.getServiceName())
                    .throwable(throwable)
                    .metadata(metadata);

            if (properties.isTraceEnabled()) {
                traceContextProvider.getCurrentTraceId().ifPresent(entryBuilder::traceId);
                traceContextProvider.getCurrentSpanId().ifPresent(entryBuilder::spanId);
            }

            LogEntry logEntry = entryBuilder.build();

            LogEntry filteredEntry = applyFilters(logEntry);
            if (filteredEntry != null) {
                processWithProcessors(filteredEntry);
                if (properties.isMetricsEnabled()) {
                    metricsCollector.incrementLogCount(level, filteredEntry.getServiceName());
                }
            }

        } catch (Exception e) {
            log.error("Error processing log entry", e);
        } finally {
            if (properties.isMetricsEnabled()) {
                long processingTime = System.currentTimeMillis() - startTime;
                metricsCollector.recordLogProcessingTime(processingTime);
            }
        }
    }

    private boolean isLevelEnabled(LogLevel level) {
        LogLevel configuredLevel = LogLevel.valueOf(properties.getLogLevel().toUpperCase());
        return level.isEnabledFor(configuredLevel);
    }

    private LogEntry applyFilters(LogEntry logEntry) {
        LogEntry current = logEntry;

        for (LogFilter filter : logFilters) {
            if (filter.shouldFilter(current)) {
                current = filter.filter(current);
                if (current == null) {
                    return null;
                }
            }
        }

        return current;
    }

    private void processWithProcessors(LogEntry logEntry) {
        logProcessors.stream()
                .filter(processor -> processor.isEnabled(logEntry))
                .forEach(processor -> {
                    try {
                        processor.process(logEntry);
                    } catch (Exception e) {
                        log.error("Error in log processor: {}", processor.getClass().getSimpleName(), e);
                        if (properties.isMetricsEnabled()) {
                            metricsCollector.incrementErrorCount(logEntry);
                        }
                    }
                });
    }

    private String getCallerClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.startsWith("com.carlosmgv02.logginglibrary")) {
                return className;
            }
        }
        return "UnknownCaller";
    }
}