package com.carlosmgv02.logginglibrary.application.usecase;

import com.carlosmgv02.logginglibrary.application.service.LoggingApplicationService;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogEntryUseCase {
    private final LoggingApplicationService loggingApplicationService;

    public void logWithMetadata(String message, LogLevel level, Map<String, Object> metadata) {
        logWithMetadata(message, level, metadata, null);
    }

    public void logWithMetadata(String message, LogLevel level, Map<String, Object> metadata, Throwable throwable) {
        loggingApplicationService.logWithMetadata(message, level, metadata, throwable);
    }
}