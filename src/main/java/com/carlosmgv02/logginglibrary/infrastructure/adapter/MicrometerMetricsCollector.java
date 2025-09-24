package com.carlosmgv02.logginglibrary.infrastructure.adapter;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;
import com.carlosmgv02.logginglibrary.domain.port.LogMetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnClass(MeterRegistry.class)
@RequiredArgsConstructor
public class MicrometerMetricsCollector implements LogMetricsCollector {
    private final MeterRegistry meterRegistry;

    private Timer processingTimer;
    private Counter errorCounter;

    @PostConstruct
    public void init() {
        this.processingTimer = Timer.builder("logging.processing.time")
                .description("Time taken to process log entries")
                .register(meterRegistry);

        this.errorCounter = Counter.builder("logging.errors")
                .description("Number of logging errors")
                .register(meterRegistry);
    }

    @Override
    public void incrementLogCount(LogLevel level, String serviceName) {
        Counter.builder("logging.entries")
                .description("Number of log entries processed")
                .tag("level", level.getName())
                .tag("service", serviceName != null ? serviceName : "unknown")
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void recordLogProcessingTime(long processingTimeMs) {
        processingTimer.record(processingTimeMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void recordLogSize(int logSizeBytes) {
        meterRegistry.summary("logging.entry.size", "unit", "bytes")
                .record(logSizeBytes);
    }

    @Override
    public void incrementErrorCount(LogEntry logEntry) {
        errorCounter.increment();
    }
}