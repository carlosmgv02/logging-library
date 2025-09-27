package com.carlosmgv02.logginglibrary.infrastructure.web;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
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

@Order(1)
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
            String traceId = traceContextProvider.getCurrentTraceId().orElse(null);
            String spanId = traceContextProvider.getCurrentSpanId().orElse(null);

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

            log.debug("HTTP Request started - Method: {}, URI: {}, TraceId: {}, SpanId: {}",
                     method, uri, traceId, spanId);

            chain.doFilter(request, response);

            log.debug("HTTP Request completed - Method: {}, URI: {}, Status: {}",
                     method, uri, httpResponse.getStatus());

        } finally {
            MDC.remove(LoggingConstants.MDC_TRACE_ID);
            MDC.remove(LoggingConstants.MDC_SPAN_ID);
            MDC.remove("correlationId");
            MDC.remove("httpMethod");
            MDC.remove("requestUri");
        }
    }
}