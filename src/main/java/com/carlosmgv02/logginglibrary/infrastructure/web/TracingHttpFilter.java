package com.carlosmgv02.logginglibrary.infrastructure.web;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import com.carlosmgv02.logginglibrary.infrastructure.context.MdcContext;
import com.carlosmgv02.logginglibrary.shared.constants.LoggingConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Map;

@Order(-1000) // Execute very early, before other filters
@RequiredArgsConstructor
@Slf4j
public class TracingHttpFilter implements Filter {

    private final TraceContextProvider traceContextProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest) ||
            !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // First try to get traceId from incoming header
            String incomingTraceId = httpRequest.getHeader(LoggingConstants.TRACE_ID_HEADER);
            String incomingSpanId = httpRequest.getHeader(LoggingConstants.SPAN_ID_HEADER);

            // Use incoming traceId if present, otherwise generate new one
            String traceId = (incomingTraceId != null && !incomingTraceId.isEmpty())
                    ? incomingTraceId
                    : traceContextProvider.getCurrentTraceId().orElse(null);

            // Use incoming spanId if present, otherwise generate new one
            String spanId = (incomingSpanId != null && !incomingSpanId.isEmpty())
                    ? incomingSpanId
                    : traceContextProvider.getCurrentSpanId().orElse(null);

            if (traceId != null && !traceId.isEmpty()) {
                MDC.put(LoggingConstants.MDC_TRACE_ID, traceId);
                httpResponse.setHeader(LoggingConstants.TRACE_ID_HEADER, traceId);
            }

            if (spanId != null && !spanId.isEmpty()) {
                MDC.put(LoggingConstants.MDC_SPAN_ID, spanId);
                httpResponse.setHeader(LoggingConstants.SPAN_ID_HEADER, spanId);
            }

            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            String correlationId = httpRequest.getHeader("X-Correlation-ID");

            if (correlationId != null && !correlationId.isEmpty()) {
                MDC.put("correlationId", correlationId);
                httpResponse.setHeader("X-Correlation-ID", correlationId);
            }

            MDC.put("httpMethod", method);
            MDC.put("requestUri", uri);

            // Backup the complete MDC context INCLUDING traceId for later restoration
            MdcContext.backup();
            log.info("💾 HTTP Filter - Backed up MDC context for later restoration");

            // Debug logging to track MDC state before proceeding
            Map<String, String> mdcBeforeChain = MDC.getCopyOfContextMap();
            log.info("🔍 HTTP Filter BEFORE chain.doFilter() - MDC: {}", mdcBeforeChain);

            log.info("🚀 HTTP Request started - Method: {}, URI: {}, TraceId: {}, SpanId: {}",
                     method, uri, traceId, spanId);

            chain.doFilter(request, response);

            // Debug logging to track MDC state after proceeding
            Map<String, String> mdcAfterChain = MDC.getCopyOfContextMap();
            log.info("🔍 HTTP Filter AFTER chain.doFilter() - MDC: {}", mdcAfterChain);

            log.info("✅ HTTP Request completed - Method: {}, URI: {}, Status: {}",
                     method, uri, httpResponse.getStatus());

        } finally {
            // Only remove HTTP-specific context, keep tracing context for the full request
            MDC.remove("httpMethod");
            MDC.remove("requestUri");
            MDC.remove("correlationId");

            // Clear backup at the end of the request
            MdcContext.clearBackup();
            log.info("🧹 HTTP Filter - Cleared backup at end of request");

            // Keep traceId and spanId available for the entire request lifecycle
            // They will be cleaned up when the thread is returned to the pool
        }
    }
}