package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.port.LogFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(SensitiveDataProperties.class)
public class SensitiveDataLogFilter implements LogFilter {
    private final SensitiveDataProperties properties;
    private static final String REPLACEMENT = "***REDACTED***";

    @Override
    public LogEntry filter(LogEntry logEntry) {
        String filteredMessage = filterSensitiveData(logEntry.getMessage());

        if (filteredMessage != null && !filteredMessage.equals(logEntry.getMessage())) {
            return logEntry.toBuilder()
                    .message(filteredMessage)
                    .build();
        }

        return logEntry;
    }

    @Override
    public boolean shouldFilter(LogEntry logEntry) {
        return properties.isEnabled() && containsSensitiveData(logEntry.getMessage());
    }

    private boolean containsSensitiveData(String message) {
        if (message == null) return false;

        return properties.getPatterns().stream()
                .anyMatch(pattern -> Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(message).find());
    }

    private String filterSensitiveData(String message) {
        if (message == null) return null;

        String filtered = message;
        for (String pattern : properties.getPatterns()) {
            filtered = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)
                    .matcher(filtered)
                    .replaceAll(REPLACEMENT);
        }

        return filtered;
    }
}