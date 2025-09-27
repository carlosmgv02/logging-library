package com.carlosmgv02.logginglibrary.shared.constants;

public final class LoggingConstants {
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String SPAN_ID_HEADER = "X-Span-Id";
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";
    public static final String MDC_SERVICE_NAME = "serviceName";

    private LoggingConstants() {
        throw new IllegalStateException("Utility class");
    }
}