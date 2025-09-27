package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import com.carlosmgv02.logginglibrary.domain.port.LogProcessor;
import com.carlosmgv02.logginglibrary.shared.constants.LoggingConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Slf4jLogProcessor implements LogProcessor {

    @Override
    public void process(LogEntry logEntry) {
        Logger logger = LoggerFactory.getLogger(logEntry.getLogger() != null ? logEntry.getLogger() : "ROOT");

        try {
            enrichMDC(logEntry);
            logWithLevel(logger, logEntry);
        } finally {
            clearMDC();
        }
    }

    @Override
    public boolean isEnabled(LogEntry logEntry) {
        return true;
    }

    private void enrichMDC(LogEntry logEntry) {
        if (logEntry.getTraceId() != null) {
            MDC.put(LoggingConstants.MDC_TRACE_ID, logEntry.getTraceId());
        }
        if (logEntry.getSpanId() != null) {
            MDC.put(LoggingConstants.MDC_SPAN_ID, logEntry.getSpanId());
        }
        if (logEntry.getServiceName() != null) {
            MDC.put(LoggingConstants.MDC_SERVICE_NAME, logEntry.getServiceName());
        }

        // Add metadata to MDC
        logEntry.getMetadata().forEach((key, value) -> {
            if (value != null) {
                MDC.put(key, value.toString());
            }
        });
    }

    private void logWithLevel(Logger logger, LogEntry logEntry) {
        String message = logEntry.getMessage();
        Throwable throwable = logEntry.getThrowable();

        switch (logEntry.getLevel()) {
            case TRACE -> {
                if (throwable != null) logger.trace(message, throwable);
                else logger.trace(message);
            }
            case DEBUG -> {
                if (throwable != null) logger.debug(message, throwable);
                else logger.debug(message);
            }
            case INFO -> {
                if (throwable != null) logger.info(message, throwable);
                else logger.info(message);
            }
            case WARN -> {
                if (throwable != null) logger.warn(message, throwable);
                else logger.warn(message);
            }
            case ERROR -> {
                if (throwable != null) logger.error(message, throwable);
                else logger.error(message);
            }
        }
    }

    private void clearMDC() {
        MDC.remove(LoggingConstants.MDC_TRACE_ID);
        MDC.remove(LoggingConstants.MDC_SPAN_ID);
        MDC.remove(LoggingConstants.MDC_SERVICE_NAME);
    }
}