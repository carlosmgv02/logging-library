package com.carlosmgv02.logginglibrary.shared.constants;

public final class LoggingConstants {
    public static final String DEFAULT_SERVICE_NAME = "undefined-service";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String SPAN_ID_HEADER = "X-Span-Id";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";
    public static final String MDC_SERVICE_NAME = "serviceName";

    public static final int DEFAULT_LOG_BUFFER_SIZE = 1000;
    public static final long DEFAULT_FLUSH_INTERVAL_MS = 5000L;

    private LoggingConstants() {
        throw new IllegalStateException("Utility class");
    }
}