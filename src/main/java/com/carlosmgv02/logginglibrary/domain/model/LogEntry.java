package com.carlosmgv02.logginglibrary.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public final class LogEntry {
    private final String message;
    private final LogLevel level;
    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final String logger;
    private final String traceId;
    private final String spanId;
    private final String serviceName;
    private final Throwable throwable;
    @Builder.Default
    private final Map<String, Object> metadata = Map.of();
}