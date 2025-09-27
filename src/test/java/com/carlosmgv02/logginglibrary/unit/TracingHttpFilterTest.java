package com.carlosmgv02.logginglibrary.unit;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import com.carlosmgv02.logginglibrary.infrastructure.web.TracingHttpFilter;
import com.carlosmgv02.logginglibrary.shared.constants.LoggingConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TracingHttpFilterTest {

    @Mock
    private TraceContextProvider traceContextProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TracingHttpFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TracingHttpFilter(traceContextProvider);
        MDC.clear();
    }

    @Test
    void shouldAddTraceAndSpanIdToMDCAndHeaders() throws ServletException, IOException {
        String expectedTraceId = "test-trace-123";
        String expectedSpanId = "test-span-456";

        when(traceContextProvider.getCurrentTraceId()).thenReturn(Optional.of(expectedTraceId));
        when(traceContextProvider.getCurrentSpanId()).thenReturn(Optional.of(expectedSpanId));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(LoggingConstants.TRACE_ID_HEADER, expectedTraceId);
        verify(response).setHeader(LoggingConstants.SPAN_ID_HEADER, expectedSpanId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleCorrelationId() throws ServletException, IOException {
        String correlationId = "correlation-123";
        String expectedTraceId = "test-trace-123";
        String expectedSpanId = "test-span-456";

        when(traceContextProvider.getCurrentTraceId()).thenReturn(Optional.of(expectedTraceId));
        when(traceContextProvider.getCurrentSpanId()).thenReturn(Optional.of(expectedSpanId));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Correlation-ID")).thenReturn(correlationId);

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader("X-Correlation-ID", correlationId);
        verify(response).setHeader(LoggingConstants.TRACE_ID_HEADER, expectedTraceId);
        verify(response).setHeader(LoggingConstants.SPAN_ID_HEADER, expectedSpanId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleEmptyTraceContext() throws ServletException, IOException {
        when(traceContextProvider.getCurrentTraceId()).thenReturn(Optional.empty());
        when(traceContextProvider.getCurrentSpanId()).thenReturn(Optional.empty());
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilter(request, response, filterChain);

        verify(response, never()).setHeader(eq(LoggingConstants.TRACE_ID_HEADER), any());
        verify(response, never()).setHeader(eq(LoggingConstants.SPAN_ID_HEADER), any());
        verify(filterChain).doFilter(request, response);
    }
}