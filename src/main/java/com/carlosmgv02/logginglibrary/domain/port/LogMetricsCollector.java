package com.carlosmgv02.logginglibrary.domain.port;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;
import com.carlosmgv02.logginglibrary.domain.model.LogLevel;

public interface LogMetricsCollector {
    void incrementLogCount(LogLevel level, String serviceName);
    void recordLogProcessingTime(long processingTimeMs);
    void recordLogSize(int logSizeBytes);
    void incrementErrorCount(LogEntry logEntry);
}