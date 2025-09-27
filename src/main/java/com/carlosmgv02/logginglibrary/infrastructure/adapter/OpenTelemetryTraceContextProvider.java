package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConditionalOnClass(Span.class)
public class OpenTelemetryTraceContextProvider implements TraceContextProvider {

    @Override
    public Optional<String> getCurrentTraceId() {
        try {
            Span currentSpan = Span.current();
            if (currentSpan != null) {
                SpanContext spanContext = currentSpan.getSpanContext();
                if (spanContext.isValid()) {
                    return Optional.of(spanContext.getTraceId());
                }
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> getCurrentSpanId() {
        try {
            Span currentSpan = Span.current();
            if (currentSpan != null) {
                SpanContext spanContext = currentSpan.getSpanContext();
                if (spanContext.isValid()) {
                    return Optional.of(spanContext.getSpanId());
                }
            }
        } catch (Exception e) {
            // Silently ignore OpenTelemetry errors
        }
        return Optional.empty();
    }

    @Override
    public boolean isTraceActive() {
        try {
            Span currentSpan = Span.current();
            return currentSpan != null && currentSpan.getSpanContext().isValid();
        } catch (Exception e) {
            return false;
        }
    }
}