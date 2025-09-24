package com.carlosmgv02.logginglibrary.unit;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LogEntryTest {

    @Test
    void shouldBuildLogEntryWithRequiredFields() {
        LogEntry logEntry = LogEntry.builder()
                .message("Test message")
                .level(LogLevel.INFO)
                .build();

        assertThat(logEntry.getMessage()).isEqualTo("Test message");
        assertThat(logEntry.getLevel()).isEqualTo(LogLevel.INFO);
        assertThat(logEntry.getTimestamp()).isNotNull();
        assertThat(logEntry.getMetadata()).isEmpty();
    }

    @Test
    void shouldBuildLogEntryWithAllFields() {
        Instant now = Instant.now();
        Map<String, Object> metadata = Map.of("key1", "value1", "key2", 42);
        RuntimeException exception = new RuntimeException("Test exception");

        LogEntry logEntry = LogEntry.builder()
                .message("Test message")
                .level(LogLevel.ERROR)
                .timestamp(now)
                .logger("TestLogger")
                .traceId("trace-123")
                .spanId("span-456")
                .serviceName("test-service")
                .throwable(exception)
                .metadata(metadata)
                .build();

        assertThat(logEntry.getMessage()).isEqualTo("Test message");
        assertThat(logEntry.getLevel()).isEqualTo(LogLevel.ERROR);
        assertThat(logEntry.getTimestamp()).isEqualTo(now);
        assertThat(logEntry.getLogger()).isEqualTo("TestLogger");
        assertThat(logEntry.getTraceId()).isEqualTo("trace-123");
        assertThat(logEntry.getSpanId()).isEqualTo("span-456");
        assertThat(logEntry.getServiceName()).isEqualTo("test-service");
        assertThat(logEntry.getThrowable()).isEqualTo(exception);
        assertThat(logEntry.getMetadata()).containsExactlyInAnyOrderEntriesOf(metadata);
    }

    @Test
    void shouldSupportToBuilder() {
        LogEntry original = LogEntry.builder()
                .message("Original message")
                .level(LogLevel.INFO)
                .build();

        LogEntry modified = original.toBuilder()
                .message("Modified message")
                .level(LogLevel.WARN)
                .build();

        assertThat(original.getMessage()).isEqualTo("Original message");
        assertThat(original.getLevel()).isEqualTo(LogLevel.INFO);
        assertThat(modified.getMessage()).isEqualTo("Modified message");
        assertThat(modified.getLevel()).isEqualTo(LogLevel.WARN);
    }

    @Test
    void shouldHandleEqualsAndHashCode() {
        LogEntry entry1 = LogEntry.builder()
                .message("Test message")
                .level(LogLevel.INFO)
                .logger("TestLogger")
                .build();

        LogEntry entry2 = LogEntry.builder()
                .message("Test message")
                .level(LogLevel.INFO)
                .logger("TestLogger")
                .build();

        assertThat(entry1).isNotEqualTo(entry2); // Different timestamps
        assertThat(entry1.hashCode()).isNotEqualTo(entry2.hashCode());
    }
}