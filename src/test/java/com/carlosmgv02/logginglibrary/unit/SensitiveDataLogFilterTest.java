package com.carlosmgv02.logginglibrary.unit;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import com.carlosmgv02.logginglibrary.infrastructure.adapter.SensitiveDataLogFilter;
import com.carlosmgv02.logginglibrary.infrastructure.adapter.SensitiveDataProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveDataLogFilterTest {

    private SensitiveDataLogFilter filter;
    private SensitiveDataProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SensitiveDataProperties();
        properties.setEnabled(true);
        properties.setPatterns(List.of(
                "(?i)password[\\s]*[:=][\\s]*\\S+",
                "(?i)token[\\s]*[:=][\\s]*\\S+",
                "\\b\\d{16}\\b"
        ));
        filter = new SensitiveDataLogFilter(properties);
    }

    @Test
    void shouldFilterPasswordFromMessage() {
        LogEntry logEntry = LogEntry.builder()
                .message("User login with password=secret123")
                .level(LogLevel.INFO)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);
        LogEntry filtered = filter.filter(logEntry);

        assertThat(shouldFilter).isTrue();
        assertThat(filtered.getMessage()).isEqualTo("User login with ***REDACTED***");
    }

    @Test
    void shouldFilterTokenFromMessage() {
        LogEntry logEntry = LogEntry.builder()
                .message("API call with token: abc123def456")
                .level(LogLevel.DEBUG)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);
        LogEntry filtered = filter.filter(logEntry);

        assertThat(shouldFilter).isTrue();
        assertThat(filtered.getMessage()).isEqualTo("API call with ***REDACTED***");
    }

    @Test
    void shouldFilterCreditCardNumber() {
        LogEntry logEntry = LogEntry.builder()
                .message("Processing payment for card 4532123456789012")
                .level(LogLevel.INFO)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);
        LogEntry filtered = filter.filter(logEntry);

        assertThat(shouldFilter).isTrue();
        assertThat(filtered.getMessage()).isEqualTo("Processing payment for card ***REDACTED***");
    }

    @Test
    void shouldNotFilterNormalMessage() {
        LogEntry logEntry = LogEntry.builder()
                .message("Normal log message without sensitive data")
                .level(LogLevel.INFO)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);

        assertThat(shouldFilter).isFalse();
    }

    @Test
    void shouldNotFilterWhenDisabled() {
        properties.setEnabled(false);
        filter = new SensitiveDataLogFilter(properties);

        LogEntry logEntry = LogEntry.builder()
                .message("User login with password=secret123")
                .level(LogLevel.INFO)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);

        assertThat(shouldFilter).isFalse();
    }

    @Test
    void shouldHandleNullMessage() {
        LogEntry logEntry = LogEntry.builder()
                .message(null)
                .level(LogLevel.INFO)
                .build();

        boolean shouldFilter = filter.shouldFilter(logEntry);
        LogEntry filtered = filter.filter(logEntry);

        assertThat(shouldFilter).isFalse();
        assertThat(filtered).isEqualTo(logEntry);
    }
}