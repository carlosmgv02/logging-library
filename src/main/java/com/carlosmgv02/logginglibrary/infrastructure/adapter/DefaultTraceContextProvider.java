package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import com.carlosmgv02.logginglibrary.domain.port.TraceContextProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@ConditionalOnMissingBean(TraceContextProvider.class)
public class DefaultTraceContextProvider implements TraceContextProvider {

    @Override
    public Optional<String> getCurrentTraceId() {
        // Generate a simple UUID-based trace ID when no other provider is available
        return Optional.of(UUID.randomUUID().toString().replace("-", ""));
    }

    @Override
    public Optional<String> getCurrentSpanId() {
        // Generate a simple UUID-based span ID when no other provider is available
        return Optional.of(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
    }

    @Override
    public boolean isTraceActive() {
        return true;
    }
}